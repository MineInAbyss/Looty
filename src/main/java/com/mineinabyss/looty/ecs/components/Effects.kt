package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.idofront.serialization.PotionSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect

@Serializable
@SerialName("looty:potion")
data class PotionComponent(
        val effects: List<@Serializable(with=PotionSerializer::class) PotionEffect>
) : GearyComponent
