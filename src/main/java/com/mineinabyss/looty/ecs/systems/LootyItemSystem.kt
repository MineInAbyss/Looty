package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.LootyType
import java.util.*

abstract class LootyItemSystem(interval: Long = 1) : TickingSystem(interval) {
    protected val lootyType by get<LootyType>()
    protected val uuid by get<UUID>()
}
