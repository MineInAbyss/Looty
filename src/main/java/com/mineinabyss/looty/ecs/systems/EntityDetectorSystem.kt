package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.helpers.serialName
import com.mineinabyss.idofront.messaging.broadcast

object EntityDetectorSystem : TickingSystem(interval = 300) {
    override fun GearyEntity.tick() {
        broadcast("Entity ${this.id} has ${this.getComponents().map { it.serialName }}")
    }
}
