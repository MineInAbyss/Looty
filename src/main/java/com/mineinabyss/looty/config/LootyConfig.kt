package com.mineinabyss.looty.config

import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.looty.looty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object LootyConfig : IdofrontConfig<LootyConfig.Data>(looty, Data.serializer()) {
    @Serializable
    class Data(
        val debug: Boolean = false,
        @SerialName("durability_lore_message") val durabilityLoreMessage: String,
    )
}
