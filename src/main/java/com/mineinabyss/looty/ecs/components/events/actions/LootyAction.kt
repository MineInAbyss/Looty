package com.mineinabyss.looty.ecs.components.events.actions

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.idofront.messaging.broadcast
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LootyAction {
    abstract fun runOn(entity: GearyEntity)
}


@Serializable
@SerialName("debug")
class DebugAction(
        val msg: String
) : LootyAction() {
    override fun runOn(entity: GearyEntity) {
        broadcast(msg)
    }
}

@Serializable
@SerialName("entity")
class EntityAction(
        val components: Set<GearyComponent>
) : LootyAction() {
    override fun runOn(entity: GearyEntity) {
        Engine.entity {
            addComponents(components)
        }
    }
}

@Serializable
sealed class ComponentAction : LootyAction() {
    abstract val components: Set<String>

    val componentClasses by lazy {
        components.map {
            Formats.componentSerialNames[it] ?: error("$it is not a valid component name")
        }
    }
}

@Serializable
@SerialName("remove")
data class RemoveComponents(override val components: Set<String>) : ComponentAction() {
    override fun runOn(entity: GearyEntity) {
        componentClasses.forEach {
            Engine.removeComponentFor(it, entity.gearyId)
        }
    }
}

@Serializable
@SerialName("disable")
class DisableComponents(override val components: Set<String>) : ComponentAction() {
    override fun runOn(entity: GearyEntity) {
        componentClasses.forEach {
            Engine.disableComponentFor(it, entity.gearyId)
        }
    }
}

@Serializable
@SerialName("enable")
data class EnableComponents(override val components: Set<String>) : ComponentAction() {
    override fun runOn(entity: GearyEntity) {
        componentClasses.forEach {
            Engine.enableComponentFor(it, entity.gearyId)
        }
    }
}

@Serializable
@SerialName("add")
class AddComponentAction(
        val components: Set<GearyComponent>
) : LootyAction() {
    override fun runOn(entity: GearyEntity) {
        entity.addComponents(components)
    }
}
