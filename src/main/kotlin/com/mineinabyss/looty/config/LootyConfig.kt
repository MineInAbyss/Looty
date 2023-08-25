package com.mineinabyss.looty.config

import kotlinx.serialization.Serializable

@Serializable
data class LootyConfig(
    val debug: Boolean = false,
    val migrateByCustomModelData: Boolean = false
)
