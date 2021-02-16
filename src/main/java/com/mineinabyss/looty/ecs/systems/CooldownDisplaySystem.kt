package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.CooldownManager
import com.mineinabyss.geary.ecs.components.parent
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import org.bukkit.ChatColor
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

object CooldownDisplaySystem : TickingSystem(interval = 10) {
    @ExperimentalTime
    override fun tick() = Engine.forEach<SlotType.Held, CooldownManager> { _, cooldownManager ->
        parent?.with<PlayerComponent>() { (player) ->
            player.sendActionBar(cooldownManager.incompleteCooldowns.entries.joinToString("\n") { (key, cooldown) ->
                val displayLength = 10
                val displayChar = '■'
                val length = cooldown.length.milliseconds
                val timeLeft = (cooldown.endTime - System.currentTimeMillis()).milliseconds
                val squaresLeft = if(timeLeft.inSeconds * 20 < 10) 0 else (timeLeft / length * displayLength).roundToInt()

                buildString {
                    append("$key ")
                    append(ChatColor.GREEN)
                    repeat (displayLength - squaresLeft) {
                        append(displayChar)
                    }
                    append(ChatColor.RED)
                    repeat (squaresLeft) {
                        append(displayChar)
                    }
                    append(ChatColor.GRAY)
                    append(" [${timeLeft.toString()}]")
                }
            })
        }
    }
}
