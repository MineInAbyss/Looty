package com.mineinabyss.looty.features.consumable

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("looty:requires_consumable")
class RequiresConsumable(
    val type: SerializableItemStack,
    val minAmount: Int = 1,
)

@AutoScan
fun GearyModule.createRequiresConsumableCondition() = listener(
    object : ListenerQuery() {
        val player by get<Player>()
        val condition by source.get<RequiresConsumable>()
    }
).check {
    val matchedItem = player.inventory.firstOrNull { condition.type.matches(it) } ?: return@check false
    matchedItem.amount >= condition.minAmount
}

