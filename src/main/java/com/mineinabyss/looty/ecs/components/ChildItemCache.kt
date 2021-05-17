package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.components.ItemComponent
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import org.bukkit.inventory.ItemStack
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class ChildItemCache {
    //TODO don't use a map, some better array structure instead.
    val itemMap get() = _itemMap.toMutableMap()
    private val _itemMap: MutableMap<Int, GearyEntity> = mutableMapOf()

    operator fun get(slot: Int): GearyEntity? = _itemMap[slot]

    /** Updates the ItemStack reference for the entity in this [slot]. */
    fun update(slot: Int, item: ItemStack) {
        _itemMap[slot]?.get<ItemComponent>()?.item = item
    }

    fun update(slot: Int, edit: ItemStack.() -> Unit) {
        _itemMap[slot]?.get<ItemComponent>()?.item?.apply(edit)
    }

    /** Adds a new entity into a specified [slot], given an accompanying [item] */
    fun add(slot: Int, entity: GearyEntity, item: ItemStack) {
        //remove the old entity from ECS entirely and replace with the new one
        remove(slot)
        _itemMap[slot] = entity
        entity.set(ItemComponent(item, slot))
        debug("Added to $slot")

        // If in armor slots
        if (slot in 36..39) //TODO make version safe!
            entity.add<SlotType.Equipped>()
    }


    /** Moves entity in [fromSlot] to [toSlot], overriding whatever was in [toSlot] previously. */
    fun move(fromSlot: Int, toSlot: Int) {

    }

    /** Stops tracking entity in [slot] and removes it from ECS. */
    fun remove(slot: Int) {
        _itemMap[slot]?.removeEntity() ?: return
        debug("Removed from $slot")
        _itemMap -= slot
    }

    /*fun swapHeldComponent(removeFrom: Int, addTo: Int) {
        this[removeFrom]?.removeComponent<Held>()
        this[addTo]?.addComponent(Held())
    }*/

    /** Swaps the slots of two existing components. Either can be null. */
    fun swap(first: Int, second: Int) {
        val firstTemp = _itemMap[first]
        val secondTemp = _itemMap[second]

        // null safety forces us to remove the item from the map if it's null
        if (firstTemp == null) _itemMap.remove(second)
        else _itemMap[second] = firstTemp

        if (secondTemp == null) _itemMap.remove(first)
        else _itemMap[first] = secondTemp
        debug("Swapped $first and $second")
    }

    internal fun clear() {
        _itemMap.keys.toList().forEach { remove(it) }
    }
}

private operator fun <T> WeakReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = get()
