package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.PotionEffectSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect

@Serializable
@SerialName("looty:potion")
@AutoscanComponent
data class PotionComponent(
    val effects: List<@Serializable(with=PotionEffectSerializer::class) PotionEffect>
)
