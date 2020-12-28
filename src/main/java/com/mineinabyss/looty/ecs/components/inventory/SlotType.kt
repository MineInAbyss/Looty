package com.mineinabyss.looty.ecs.components.inventory

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SlotType {
    @Serializable
    @SerialName("looty:slot.held")
    object Held: GearyComponent

    @Serializable
    @SerialName("looty:slot.offhand")
    object Offhand: GearyComponent

    @Serializable
    @SerialName("looty:slot.hotbar")
    object Hotbar: GearyComponent
}
