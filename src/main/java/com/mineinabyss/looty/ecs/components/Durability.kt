package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:durability_item_state")
@AutoscanComponent
data class DurabilityItemState(
    val item: SerializableItemStack,
    val min: Int,
    val max: Int
)

@Serializable
@SerialName("looty:durability_states")
@AutoscanComponent
data class DurabilityState(val states: List<DurabilityItemState>)
