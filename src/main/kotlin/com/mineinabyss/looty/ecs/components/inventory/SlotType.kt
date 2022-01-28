package com.mineinabyss.looty.ecs.components.inventory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SlotType {
    @Serializable
    @SerialName("looty:slot.held")
    object Held

    @Serializable
    @SerialName("looty:slot.offhand")
    object Offhand

    @Serializable
    @SerialName("looty:slot.hotbar")
    object Hotbar

    @Serializable
    @SerialName("looty:slot.equipped")
    object Equipped
}
