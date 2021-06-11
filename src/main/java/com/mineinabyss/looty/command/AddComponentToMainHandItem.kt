package com.mineinabyss.looty.command

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.tracking.gearyOrNull
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerializationException
import org.bukkit.entity.Player

fun addRawJSONComponent(player: Player, json: String): Unit
{
    try {
        val item = gearyOrNull(player.inventory.itemInMainHand) ?: return
        val component = Formats.jsonFormat.decodeFromString(PolymorphicSerializer(GearyComponent::class), json)
        item.set(component, component::class)
    } catch (e: SerializationException) {
        player.info(e.message)
    }
}