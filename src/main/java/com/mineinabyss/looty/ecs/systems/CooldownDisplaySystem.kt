package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.CooldownManager
import com.mineinabyss.geary.ecs.components.parent
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import kotlin.math.floor
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

object CooldownDisplaySystem : TickingSystem(interval = 10) {
    @ExperimentalTime
    override fun tick() = Engine.forEach<CooldownManager> { cooldownManager ->
        parent?.with<PlayerComponent>() { (player) ->
            player.sendActionBar(cooldownManager.incompleteCooldowns.entries.joinToString("\n") { (key, value) ->
                "$key [${floor((value - System.currentTimeMillis()).milliseconds.inSeconds * 10) / 10}s]"
            })
        }
    }
}
