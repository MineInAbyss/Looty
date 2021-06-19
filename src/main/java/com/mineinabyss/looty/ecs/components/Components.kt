package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:max_durability")
@AutoscanComponent
data class MaxDurabilityComponent(val maxDurability: Int)

@Serializable
@SerialName("looty:min_durability")
@AutoscanComponent
data class MinDurabilityComponent(val minDurability: Int = 0)

@Serializable
@SerialName("looty:durability")
@AutoscanComponent
data class DurabilityComponent(var durability: Int)

@Serializable
@SerialName("looty:durability_depleted")
@AutoscanComponent
data class DurabilityDepletedComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:broken")
@AutoscanComponent
data class BrokenComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:consumable")
@AutoscanComponent
data class ConsumeOnDurabilityDepleteComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:broken_item")
@AutoscanComponent
data class BrokenItemComponent(val item: SerializableItemStack)

@Serializable
@SerialName("looty:context_item_replacement")
@AutoscanComponent
data class ReplaceContextItemComponent(val item: SerializableItemStack)
