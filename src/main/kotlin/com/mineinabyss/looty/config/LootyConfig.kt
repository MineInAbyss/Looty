package com.mineinabyss.looty.config

import com.mineinabyss.looty.looty
import kotlinx.serialization.Serializable

val lootyConfig get() = looty.config.data
@Serializable
data class LootyConfig(
    val debug: Boolean = false,
    val migrateByCustomModelData: Boolean = false
)
