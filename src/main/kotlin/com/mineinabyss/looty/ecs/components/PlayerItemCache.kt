package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.helpers.NO_ENTITY
import com.mineinabyss.geary.helpers.toGeary
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.looty.debug
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

// TODO bad pattern, passing entity into component, move into event
class PlayerItemCache(parent: GearyEntity) {
    private val entities = ULongArray(64)
    private val cachedItems = Array<NMSItemStack?>(64) { null }
    private val playerInstanced = PlayerInstancedItems(parent)

    fun swap(firstSlot: Int, secondSlot: Int) {
        val firstEntity = entities[firstSlot].toGeary()
        val secondEntity = entities[secondSlot].toGeary()
        if (firstEntity in playerInstanced) {
            playerInstanced.setSlot(firstEntity, secondSlot)
            playerInstanced.unsetSlot(firstEntity, firstSlot, false)
        }
        if (secondEntity in playerInstanced) {
            playerInstanced.setSlot(secondEntity, firstSlot)
            playerInstanced.unsetSlot(secondEntity, secondSlot, false)
        }
        entities[firstSlot] = secondEntity.id
        entities[secondSlot] = firstEntity.id
        debug("Swapped ${firstEntity.get<PrefabKey>()} in $firstSlot and ${secondEntity.get<PrefabKey>()} in $secondSlot")
    }

    fun move(oldSlot: Int, newSlot: Int) {
        val entity = entities[oldSlot].takeIf { it != 0uL }?.toGeary() ?: return
        if (entity in playerInstanced) {
            playerInstanced.setSlot(entity, newSlot)
            playerInstanced.unsetSlot(entity, oldSlot, false)
        }
        entities[newSlot] = entity.id
        entities[oldSlot] = 0uL
        debug("Moved ${entity.get<PrefabKey>()} from $oldSlot to $newSlot")
    }

    /**
     * @return Whether the entity would be removed if [removeEntity] were true.
     */
    fun remove(slot: Int, removeEntity: Boolean): Boolean {
        val entity = entities[slot].toGeary()
        if (entity.id == 0uL) return false
        entities[slot] = 0uL
        cachedItems[slot] = null
        return if (entity.has<PlayerInstancedItem>())
            playerInstanced.unsetSlot(entity, slot, removeEntity)
        else if (removeEntity) {
            entity.removeEntity()
            true
        } else false
    }

    operator fun set(slot: Int, entity: GearyEntity) {
        entities[slot] = entity.id
        if (entity.has<PlayerInstancedItem>())
            playerInstanced.setSlot(entity, slot)
    }

    operator fun get(slot: Int): GearyEntity = entities[slot].toGeary()

    fun getInstance(prefab: GearyEntity) = playerInstanced[prefab]

    fun updateItem(slot: Int, item: NMSItemStack) {
        // Get the instance of the prefab if the entity is a prefab

        cachedItems[slot] = item
        val entity = entities[slot].toGeary()
        if(entity == NO_ENTITY) return
        val prefab = playerInstanced[entity]
        (prefab ?: entity).set<ItemStack>(CraftItemStack.asCraftMirror(item))
    }

    fun getItem(slot: Int): NMSItemStack? {
        return cachedItems[slot]
    }
}
