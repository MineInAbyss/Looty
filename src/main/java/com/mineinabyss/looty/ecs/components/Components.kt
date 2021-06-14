package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.engine.componentId
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.serialization.Formats
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
data class CurrentDurabilityComponent(var currentDurability: Int)

@Serializable
@SerialName("looty:decreaseDurability")
@AutoscanComponent
data class DecreaseDurabilityComponent(val delta: Int = 1)

@Serializable
@SerialName("looty:durabilityDepleted")
@AutoscanComponent
data class DurabilityDepletedComponent(val noFields: Boolean = true)

@Serializable
@SerialName("looty:broken")
@AutoscanComponent
data class BrokenComponent(val noFields: Boolean = true)

@Serializable
@SerialName("looty:consumeOnDurabilityDeplete")
@AutoscanComponent
data class ConsumeOnDurabilityDepleteComponent(val noFields: Boolean = true)

@Serializable
@SerialName("looty:putInABrokenStateOnDurabilityDeplete")
@AutoscanComponent
data class PutInABrokenStateDurabilityDepleteComponent(val item: SerializableItemStack)

@Serializable
@SerialName("looty:putInABrokenStateOnDurabilityDeplete")
@AutoscanComponent
data class UpdateContextItemComponent(val item: SerializableItemStack)


//Will be moved to geary
@Serializable
@SerialName("geary:not_entity")
class NotOnEntityComponentConditions(
    @SerialName("not") val components: Set<String> = emptySet(),
) : GearyCondition() {
    private val componentClasses by lazy { components.map { Formats.getClassFor(it) } }

    override fun GearyEntity.check(): Boolean =
        !componentClasses.fold(false) { acc, it -> acc || has(componentId(it)) }
}
