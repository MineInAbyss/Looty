package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:discoverRecipe")
@AutoscanComponent
class DiscoverRecipe(
    val discoverRecipe: Boolean = false
)
