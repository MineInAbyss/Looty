package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("debug:screaming")
@AutoscanComponent
data class Screaming(
    val scream: String = "AAAAAAAAAAAA"
)
