package com.mineinabyss.looty.config

import com.mineinabyss.idofront.di.DI
import kotlinx.serialization.Serializable

val lootyConfig by DI.observe<LootyConfig>()

@Serializable
data class LootyConfig(
    val debug: Boolean = false,
    val migrateByCustomModelData: Boolean = false
)
