package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:maxDurability")
@AutoscanComponent
data class MaxDurabilityComponent(val maxDurability: Int)

@Serializable
@SerialName("looty:minDurability")
@AutoscanComponent
data class MinDurabilityComponent(val minDurability: Int = 0)

@Serializable
@SerialName("looty:durability")
@AutoscanComponent
data class DurabilityComponent(var durability: Int)

@Serializable
@SerialName("looty:durabilityDepleted")
@AutoscanComponent
data class DurabilityDepletedComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:broken")
@AutoscanComponent
data class BrokenComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:consumeOnDurabilityDeplete")
@AutoscanComponent
data class ConsumeOnDurabilityDepleteComponent(val empty: Boolean = true)

@Serializable
@SerialName("looty:putInABrokenStateOnDurabilityDeplete")
@AutoscanComponent
data class PutInABrokenStateDurabilityDepleteComponent(val item: SerializableItemStack)

@Serializable
@SerialName("looty:putInABrokenStateOnDurabilityDeplete")
@AutoscanComponent
data class UpdateContextItemComponent(val item: SerializableItemStack)
