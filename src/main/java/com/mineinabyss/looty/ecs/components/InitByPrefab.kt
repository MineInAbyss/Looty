package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@AutoscanComponent
@SerialName("looty:init_by_prefab")
data class InitByPrefab (val prefabTypeName: String)
