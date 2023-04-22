package com.mineinabyss.looty.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:original_display_name")
class OriginalDisplayName(var originalDisplayName: String?)
