package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.LootyType
import java.util.*

abstract class LootyItemSystem(interval: Long = 1) : TickingSystem(interval) {
    protected val QueryResult.lootyType by get<LootyType>()
    protected val QueryResult.uuid by get<UUID>()
}
