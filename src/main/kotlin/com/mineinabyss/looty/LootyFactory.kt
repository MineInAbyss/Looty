package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.entities.toGeary
import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.ecs.engine.INSTANCEOF
import com.mineinabyss.geary.ecs.engine.hasRole
import com.mineinabyss.geary.ecs.entities.RegenerateUUIDOnClash
import com.mineinabyss.geary.ecs.entities.addParent
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.store.decodeComponents
import com.mineinabyss.geary.papermc.store.decodeComponentsFrom
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.geary.papermc.store.encodePrefabs
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.PlayerInstancedItem
import com.mineinabyss.looty.ecs.components.PlayerInstancedItems
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventorySlotContext
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerSingletonContext
import com.mineinabyss.looty.ecs.components.itemcontexts.ProcessingItemContext
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

/**
 * Many helper functions related to creating Looty items.
 */
object LootyFactory {
    /** Creates an ItemStack from a [prefabKey], encoding relevant information to it. */
    fun createFromPrefab(
        prefabKey: PrefabKey,
    ): ItemStack {
        val item = ItemStack(Material.STICK)
        item.editMeta { meta ->
            encodeFromPrefab(item, meta, prefabKey)
        }
        return item
    }

    fun encodeFromPrefab(item: ItemStack, meta: ItemMeta, prefabKey: PrefabKey) {
        val prefab = prefabKey.toEntity() ?: return
        prefab.get<LootyType>()?.item?.updateMeta(item, meta)
        meta.persistentDataContainer.encodePrefabs(listOf(prefabKey))
    }


    fun addSlotTypeComponent(context: PlayerInventorySlotContext, entity: GearyEntity) = with(context) {
        entity.apply {
            remove<SlotType.Equipped>()
            remove<SlotType.Offhand>()
            remove<SlotType.Held>()

            when (slot) {
                in 36..39 -> add<SlotType.Equipped>()
                40 -> add<SlotType.Offhand>()
            }
            if (slot == inventory.heldItemSlot) add<SlotType.Held>()
        }
    }
}

/** Gets or creates a [GearyEntity] based on a given item and the context it is in. */
fun PlayerInventorySlotContext.loadItem(context: ProcessingItemContext, ): GearyEntity? = with(context) {
//    if (!hasComponentsEncoded) return null
    val gearyPlayer = holder.toGeary()
    val decoded = meta.persistentDataContainer.decodeComponents()

    // Attempt to load player-instanced item into a component on the player
    val prefabs = decoded.type.filter { it.hasRole(INSTANCEOF) }
    if (prefabs.size == 1) {
        val prefab = prefabs.first().toGeary()
        if (prefab.has<PlayerInstancedItem>()) {
            return gearyPlayer.getOrSet { PlayerInstancedItems() }
                .load(prefab.get<PrefabKey>() ?: error("Prefab has no key"), gearyPlayer)
                .apply {
                    // Make sure the context is set on the shared entity, and add the slot of this item to it.
                    val added = getOrSet { PlayerSingletonContext(holder) }.itemSlots.add(slot)
                    if (added) with { type: LootyType ->
                        //Update the loaded item to match the item defined in LootyType
                        type.updateItem(meta)
                    }
                }
        }
    }
    // If the item is already loaded via UUID, and the slot matches, no need to load the item
    if (gearyItem?.get<PlayerInventorySlotContext>()?.slot == slot) return null

    // If the item wasn't already loaded or slots didn't match, create a new entity
    return entity {
        addParent(gearyPlayer)
        add<RegenerateUUIDOnClash>()
        decodeComponentsFrom(decoded)
        getOrSetPersisting<UUID> { UUID.randomUUID() }
        set<PlayerInventorySlotContext>(this@loadItem)
        LootyFactory.addSlotTypeComponent(this@loadItem, this)
        encodeComponentsTo(meta)
        debug("Creating item in slot $slot")
        val item = updateMeta()
        set<ItemStack>(item)
        debug("Loaded item $item")
    }
}
