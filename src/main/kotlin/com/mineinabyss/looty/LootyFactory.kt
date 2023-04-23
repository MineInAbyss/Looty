package com.mineinabyss.looty

import com.mineinabyss.geary.components.RegenerateUUIDOnClash
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.datatypes.GearyEntityType
import com.mineinabyss.geary.helpers.NO_ENTITY
import com.mineinabyss.geary.helpers.addParent
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.helpers.toGeary
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.papermc.store.*
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.idofront.textcomponents.serialize
import com.mineinabyss.looty.config.lootyConfig
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.OriginalDisplayName
import com.mineinabyss.looty.ecs.components.PlayerInstancedItem
import com.mineinabyss.looty.migration.custommodeldata.CustomItem
import com.mineinabyss.looty.migration.custommodeldata.CustomModelDataToPrefabMap
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.world.item.Items
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import java.util.*

/**
 * Many helper functions related to creating Looty items.
 */
object LootyFactory {
    /** Creates an ItemStack from a [prefabKey], encoding relevant information to it. */
    fun createFromPrefab(
        prefabKey: PrefabKey,
    ): ItemStack? {
        val item = ItemStack(Material.AIR)
        updateItemFromPrefab(item, prefabKey)
        return item.takeIf { it.type != Material.AIR }
    }

    fun updateItemFromPrefab(item: ItemStack, prefabKey: PrefabKey) {
        prefabKey.toEntityOrNull()?.get<LootyType>()?.let {lootyType ->
            val originalDisplayName = item.toGearyFromUUIDOrNull()?.get<OriginalDisplayName>()?.originalDisplayName
            val oldDisplayName = item.itemMeta?.displayName()
            val baseDisplayName = lootyType.item.apply { toItemStack(item) }.displayName?.removeItalics()

            item.editItemMeta {
                if (originalDisplayName != oldDisplayName?.serialize())
                    displayName(oldDisplayName?.removeItalics())
                else displayName(baseDisplayName)
                persistentDataContainer.encodePrefabs(listOf(prefabKey))
                persistentDataContainer.encode(OriginalDisplayName(baseDisplayName?.serialize()))
            }
        }
    }

    fun Component.removeItalics() =
        Component.text().decoration(TextDecoration.ITALIC, false).build().append(this)

    sealed class ItemState {
        abstract val entity: GearyEntity

        class Loaded(override val entity: GearyEntity, val slot: Int, val pdc: PersistentDataContainer) : ItemState()
        class Empty : ItemState() {
            override val entity: GearyEntity = NO_ENTITY
        }

        class NotLoaded(val slot: Int, val pdc: PersistentDataContainer) : ItemState() {
            override val entity: GearyEntity = NO_ENTITY
        }
    }

    private fun updateOldLootyItem(pdc: PersistentDataContainer, prefabs: Set<PrefabKey>, item: NMSItemStack) {
        val tag = item.tag ?: return
        if (!tag.contains("CustomModelData")) return
        if (prefabs.isEmpty()) {
            val prefab = CustomModelDataToPrefabMap[CustomItem(
                CraftMagicNumbers.getMaterial(item.item),
                tag.getInt("CustomModelData")
            )] ?: return
            pdc.encodeComponents(setOf(), GearyEntityType())
            pdc.encodePrefabs(listOf(prefab))
        }
    }

    //TODO maybe if the prefab has PlayerInstancedItem added to it, we should remove id?
    fun getItemState(pdc: PersistentDataContainer?, slot: Int, item: NMSItemStack): ItemState {
        if (pdc == null || item.item == Items.AIR || !pdc.hasComponentsEncoded) return ItemState.Empty()
        val prefabs = pdc.decodePrefabs()
        if (lootyConfig.migrateByCustomModelData) {
            updateOldLootyItem(pdc, prefabs, item)
        }
        if (prefabs.size == 1) {
            val prefab = prefabs.first().toEntityOrNull() ?: return ItemState.Empty()
            if (prefab.has<PlayerInstancedItem>()) {
                pdc.remove<UUID>()
                return ItemState.Loaded(prefab, slot, pdc)
            } else if (pdc.decode<UUID>() == null) {
                return ItemState.NotLoaded(slot, pdc)
            }
        }
        val uuid = pdc.decode<UUID>()
        if (uuid != null) {
            val entity = globalContextMC.uuid2entity[uuid] ?: return ItemState.NotLoaded(slot, pdc)
            return ItemState.Loaded(entity, slot, pdc)
        }
        return ItemState.Empty()
    }

    //TODO return the instance of prefab for PlayerInstanced
    /** Gets or creates a [GearyEntity] based on a given item and the context it is in. */
    fun loadItem(holder: GearyEntity, pdc: PersistentDataContainer/*, cache: PlayerItemCache*/): GearyEntity {
        val decoded = pdc.decodeComponents()

        // Attempt to load player-instanced item into a component on the player
        val prefabs = decoded.type.prefabs
        if (prefabs.size == 1) {
            val prefab = prefabs.first().toGeary()
            if (prefab.has<PlayerInstancedItem>()) return prefab
//            cache.getInstance(prefab)?.let { return it }
        }

        // If the item wasn't already loaded or slots didn't match, create a new entity
        return entity {
            addParent(holder)
            add<RegenerateUUIDOnClash>()
            loadComponentsFrom(decoded)
            getOrSetPersisting<UUID> { UUID.randomUUID() }
//            addSlotTypeComponent(itemLocation, this)
            encodeComponentsTo(pdc)
//            debug("Loaded item ${get<PrefabKey>()} in slot ${itemLocation.slot}")
            debug("Loaded new instance of prefab ${get<PrefabKey>()} on $holder")
        }
    }

}
