package com.mineinabyss.looty.ecs.components.inventory

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SlotType {
    @Serializable
    @SerialName("looty:held")
    object Held: GearyComponent

    @Serializable
    @SerialName("looty:held")
    object Offhand: GearyComponent

    @Serializable
    @SerialName("looty:hotbar")
    object Hotbar: GearyComponent
}
