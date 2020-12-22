package com.mineinabyss.looty.ecs.components.events.actions

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.idofront.messaging.logSuccess
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
@SerialName("add")
class AddComponentAction(
        val components: List<GearyComponent>
) : LootyAction() {
    override fun runOn(entity: GearyEntity) {
        logSuccess("Ran addition")
//        entity.addComponent(components)
    }
}
