package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.ecs.components.Screaming

object ScreamingSystem : TickingSystem(interval = 50) {
    private val QueryResult.screaming by get<Screaming>()

    override fun QueryResult.tick() {
        broadcast("I am screaming ${screaming.scream}")
    }
}
