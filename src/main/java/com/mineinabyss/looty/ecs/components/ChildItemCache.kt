package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.addChild
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.components.getComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.geary.minecraft.store.decodeComponents
import com.mineinabyss.geary.minecraft.store.encodeComponents
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.idofront.items.editItemMeta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer

class ChildItemCache(
        private val _itemCache: MutableMap<Int, LootyEntity> = mutableMapOf(),
        //TODO component probably shouldn't have access to the parent entity, do this in a system instead?
        private val parent: Player//InventoryHolder,
) : GearyComponent() {
    //yeah this is probably a sign this should be in a system
    private val gearyParent: GearyEntity by lazy { geary(parent)!! } //TODO !!

    //TODO getting entity
    operator fun get(slot: Int): LootyEntity? = _itemCache[slot]

    /** TODO Updates the ItemStack reference for the entity in this [slot]. */
    fun update(slot: Int) {
        _itemCache[slot] = entity
    }

    /** Adds a new entity into a specified [slot], given an accompanying [item] */
    fun add(slot: Int, item: ItemStack): GearyEntity {
        val entity = Engine.entity {
            //TODO safety with itemMeta and perhaps sidestep copying it
            addComponents(item.itemMeta.persistentDataContainer.decodeComponents())
        }
        gearyParent.addChild(entity)
        remove(slot) //TODO instead of just removing, we want to remove and keep cached which swap could then look at

        return entity
    }


    /** Moves entity in [fromSlot] to [toSlot], overriding whatever was in [toSlot] previously. */
    fun move(fromSlot: Int, toSlot: Int) {

    }

    /** Stops tracking entity in [slot] and removes it from ECS. */
    fun remove(slot: Int) {
        _itemCache[slot]?.remove()
        _itemCache -= slot
    }

    /*fun swapHeldComponent(removeFrom: Int, addTo: Int) {
        this[removeFrom]?.removeComponent<Held>()
        this[addTo]?.addComponent(Held())
    }*/

    /** Swaps the slots of two existing components. Either can be null. */
    fun swap(first: Int, second: Int) {
        val firstTemp = _itemCache[first]
        val secondTemp = _itemCache[second]

        // null safety forces us to remove the item from the map if it's null
        if (firstTemp == null) _itemCache.remove(second)
        else _itemCache[second] = firstTemp

        if (secondTemp == null) _itemCache.remove(first)
        else _itemCache[first] = secondTemp
    }
}

//TODO figure out how to store info on the ItemStack within the actual ECS.
data class LootyEntity(
        override val gearyId: Int,
        val item: ItemStack,
        var slot: Int,
) : GearyEntity {
    /** Serializes the entity's components to an [ItemStack]'s [PersistentDataContainer] */
    fun writeToItem(item: ItemStack) {
        //TODO don't clone itemMeta yet another time
        //TODO dont save if no changes found to avoid extra computations
        item.editItemMeta {
            persistentDataContainer.encodeComponents(getComponents())
        }
    }
}
