package com.mineinabyss.looty.features.consumable

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.events.CheckingListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("looty:requiresConsumable")
class RequiresConsumable(
    val type: SerializableItemStack,
    val minAmount: Int = 1,
)

@AutoScan
class RequiresConsumableCondition : CheckingListener() {
    val Pointers.player by get<Player>().on(target)
    val Pointers.condition by get<RequiresConsumable>().on(source)
    override fun Pointers.check(): Boolean {
        val matchedItem = player.inventory.firstOrNull { condition.type.matches(it) } ?: return false
        return matchedItem.amount >= condition.minAmount
    }
}
