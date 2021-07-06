package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:max_durability")
@AutoscanComponent
data class MaxDurabilityComponent(@SerialName("max_durability") val maxDurability: Int)

@Serializable
@SerialName("looty:min_durability")
@AutoscanComponent
data class MinDurabilityComponent(@SerialName("min_durability") val minDurability: Int = 0)

@Serializable
@SerialName("looty:durability")
@AutoscanComponent
data class DurabilityComponent(var durability: Int)

@Serializable
@SerialName("looty:show_durability_bar")
@AutoscanComponent
data class ShowDurabilityBar(val no_param: Boolean = true)

@Serializable
@SerialName("looty:show_durability_lore")
@AutoscanComponent
data class ShowDurabilityLore(val no_param: Boolean = true)

@Serializable
@SerialName("looty:show_durability_item")
@AutoscanComponent
data class ShowDurabilityItem(val no_param: Boolean = true)
