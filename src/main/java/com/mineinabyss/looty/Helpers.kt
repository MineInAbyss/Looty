package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.entities.addParent
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}

object LootyFactory {
    fun createFromPrefab(
        holder: GearyEntity,
        prefab: GearyEntity,
        slot: Int? = null,
        addToInventory: Boolean = false,
    ): Pair<GearyEntity, ItemStack>? {
        val type = prefab.get<LootyType>() ?: return null
        val entity = Engine.entity {
            addParent(holder)
            addPrefab(prefab)
        }

        return addToInventory(holder, entity, entity.encodeComponentsTo(type), slot, addToInventory)
    }

    fun loadFromItem(
        holder: GearyEntity,
        item: ItemStack,
        slot: Int? = null,
        addToInventory: Boolean = false,
    ): Pair<GearyEntity, ItemStack>? {
        val entity = Engine.entity {
            addParent(holder)
            decodeComponentsFrom(item.itemMeta.persistentDataContainer)
        }

        return addToInventory(holder, entity, item, slot, addToInventory)
    }

    private fun addToInventory(
        holder: GearyEntity,
        entity: GearyEntity,
        item: ItemStack,
        slot: Int? = null,
        addToInventory: Boolean = false
    ): Pair<GearyEntity, ItemStack>? {
        //TODO create an ECS version of Inventory so we don't rely on spigot
        val player = holder.get<Player>() ?: return null
        val inventory = player.inventory
        val useSlot = slot ?: inventory.firstEmpty()

        //TODO What if it's full?
        if (addToInventory) inventory.setItem(useSlot, item)

        val itemCache = holder.getOrSet { ChildItemCache() }
        itemCache.add(useSlot, entity, item)

        return entity to item
    }
}

fun GearyEntity.encodeComponentsTo(lootyType: LootyType): ItemStack =
    lootyType.item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }