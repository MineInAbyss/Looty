package com.mineinabyss.looty.command

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.tracking.gearyOrNull
import org.bukkit.entity.Player

fun removeComponentBySerialName(player: Player, name: String): Unit
{
    try {
        val kClass = Formats.getClassFor(name)
        val item = gearyOrNull(player.inventory.itemInMainHand) ?: return
        item.remove(kClass)
    } catch (e: IllegalStateException) {
        player.info(e.message)
    }
}