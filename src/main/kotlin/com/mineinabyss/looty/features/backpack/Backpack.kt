package com.mineinabyss.looty.features.backpack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:backpack")
class Backpack(
    val canOpenInInventory: Boolean = true,
    val canOpenInChest: Boolean = true,
    val canOpenInEnderChest: Boolean = true,
    val canOpenInBarrels: Boolean = true
)
