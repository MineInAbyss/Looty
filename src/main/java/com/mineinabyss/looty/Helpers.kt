package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.PrefabKey
import com.mineinabyss.geary.ecs.entities.addParent
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}

fun GearyEntity.addLooty(prefab: PrefabKey): Pair<GearyEntity, ItemStack>? {
    return addLooty(PrefabManager[prefab] ?: return null)
}

fun GearyEntity.addLooty(prefab: GearyEntity, slot: Int? = null): Pair<GearyEntity, ItemStack>? {
    val item = prefab.get<LootyType>()?.item?.toItemStack() ?: return null

    val entity = Engine.entity {
        addParent(this@addLooty)
        addPrefab(prefab)
    }

    item.editItemMeta {
        entity.encodeComponentsTo(persistentDataContainer)
    }

    return addLooty(entity, item, slot)
}

fun GearyEntity.addLooty(item: ItemStack, slot: Int? = null): Pair<GearyEntity, ItemStack>? {
    val entity = Engine.entity {
        addParent(this@addLooty)
        decodeComponentsFrom(item.itemMeta.persistentDataContainer)
    }

    return addLooty(entity, item, slot)
}


private fun GearyEntity.addLooty(
    entity: GearyEntity,
    item: ItemStack,
    slot: Int? = null
): Pair<GearyEntity, ItemStack>? {
    //TODO create an ECS version of Inventory so we don't rely on spigot
    val (player) = get<PlayerComponent>() ?: return null

    //TODO What if it's full?
    val useSlot = slot ?: player.inventory.firstEmpty()
    val itemCache = getOrSet { ChildItemCache() }

    itemCache.add(useSlot, entity, item)

    player.inventory.setItem(useSlot, item)
    return entity to item
}
