package com.mineinabyss.looty.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:pack_generation")
class LootyPackGen(
    val model: String = "",
    val texture: String = "",
)
