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
@SerialName("looty:consume_item")
class ConsumeItemFromInventory(
    val type: SerializableItemStack,
    val amount: Int = 1,
)

@AutoScan
fun GearyModule.createConsumeItemAction() = listener(
    object : ListenerQuery() {
        val player by get<Player>()
        val action by source.get<ConsumeItemFromInventory>()
    }
).exec {
    val matchedItem = player.inventory.firstOrNull { action.type.matches(it) } ?: return@exec
    matchedItem.amount -= action.amount
}

