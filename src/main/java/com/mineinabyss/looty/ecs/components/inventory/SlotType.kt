package com.mineinabyss.looty.ecs.components.inventory

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SlotType {
    @Serializable
    @SerialName("looty:slot.held")
    @AutoscanComponent
    object Held

    @Serializable
    @SerialName("looty:slot.offhand")
    @AutoscanComponent
    object Offhand

    @Serializable
    @SerialName("looty:slot.hotbar")
    @AutoscanComponent
    object Hotbar

    @Serializable
    @SerialName("looty:slot.equipped")
    @AutoscanComponent
    object Equipped
}
