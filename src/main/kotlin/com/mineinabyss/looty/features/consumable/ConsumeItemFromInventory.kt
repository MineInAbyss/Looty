package com.mineinabyss.looty.features.consumable

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.serialization.SerializableItemStack
import org.bukkit.entity.Player

class ConsumeItemFromInventory(
    val type: SerializableItemStack,
    val amount: Int = 1,
)

@AutoScan
class ConsumeItemAction : GearyListener() {
    val Pointers.player by get<Player>().on(target)
    val Pointers.action by get<ConsumeItemFromInventory>().on(source)

    override fun Pointers.handle() {
        val matchedItem = player.inventory.firstOrNull { action.type.matches(it) } ?: return
        matchedItem.amount -= action.amount
    }
}
