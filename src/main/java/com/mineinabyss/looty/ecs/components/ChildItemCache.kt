package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.geary.minecraft.components.ItemComponent
import com.mineinabyss.geary.minecraft.isGearyEntity
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.looty.debug
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class ChildItemCache(
        //TODO component probably shouldn't have access to the parent entity, do this in a system instead?
        private val parent: Player, //InventoryHolder
        //TODO don't use a map, some better array structure instead.
        private val _itemCache: MutableMap<Int, GearyEntity> = mutableMapOf(),
) : GearyComponent {
    //yeah this is probably a sign this should be in a system
    private val gearyParent: GearyEntity by lazy {
        geary(parent) ?: error("$parent was not registered with geary.")
    }

    operator fun get(slot: Int): GearyEntity? = _itemCache[slot]

    /** Updates the ItemStack reference for the entity in this [slot]. */
    fun update(slot: Int, item: ItemStack) {
        _itemCache[slot]?.get<ItemComponent>()?.item = item
    }

    fun update(slot: Int, edit: ItemStack.() -> Unit) {
        _itemCache[slot]?.get<ItemComponent>()?.item?.apply(edit)
    }

    /** Adds a new entity into a specified [slot], given an accompanying [item] */
    fun add(slot: Int, item: ItemStack): GearyEntity {
        val entity = Engine.entity {
            addComponent(ItemComponent(item, slot))
            //TODO safety with itemMeta and perhaps sidestep copying it
            decodeComponentsFrom(item.itemMeta.persistentDataContainer)
            parent = gearyParent
        }

        //remove the old entity from ECS entirely and replace with the new one
        remove(slot)
        _itemCache[slot] = entity
        debug("Added to $slot")

        return entity
    }


    /** Moves entity in [fromSlot] to [toSlot], overriding whatever was in [toSlot] previously. */
    fun move(fromSlot: Int, toSlot: Int) {

    }

    /** Stops tracking entity in [slot] and removes it from ECS. */
    fun remove(slot: Int) {
        _itemCache[slot]?.apply {
            remove()
            debug("Removed from $slot")
        }
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
        debug("Swapped $first and $second")
    }

    internal fun clear() {
        _itemCache.keys.forEach { index ->
            remove(index)
        }
    }

    //TODO If an entity is ever not removed properly from ECS but is removed from the cache, it will forever exist but
    // not be tracked. Either we need a GC or make 1000% this never fails.
    @Synchronized
    fun reevaluate(inventory: PlayerInventory) {
        val heldSlot = inventory.heldItemSlot
        //we remove any items from this copy that were modified, whatever remains will be removed
        val untouched = _itemCache.toMutableMap()
        //TODO prevent issues with children and id changes

        inventory.forEachIndexed { slot, item ->

            //================================ TODO MOVE OUT PROLLY cause we re-read meta when adding entity
            if (item == null || !item.hasItemMeta()) return@forEachIndexed
            val meta = item.itemMeta
            val container = meta.persistentDataContainer
            if (!container.isGearyEntity) return@forEachIndexed //TODO perhaps some way of knowing this without cloning the ItemMeta
            //================================

            get(slot)?.with<ItemComponent> { lootyItem ->
                //TODO if changes were made to the ECS entity, they should be re-serialized here
                //if the items match exactly, encode components to the itemstack
                if (item != lootyItem.item) {
                    add(slot, item)
                }

                untouched -= slot

                //TODO managing whether an item is in main hand/offhand/armor, etc...
                // This might be better to just evaluate as we go if we know slot in LootyEntity

            }

        }

        untouched.keys.forEach { remove(it) }
    }
}
