package com.mineinabyss.looty.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:resourcepack")
class LootyPack(
    val model: String = "",
    val texture: String = "",
)
