package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.CooldownManager
import com.mineinabyss.geary.ecs.entities.parent
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.map.MinecraftFont
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

private const val INTERVAL = 3L

object CooldownDisplaySystem : TickingSystem(interval = INTERVAL) {
    private val held = has<SlotType.Held>()
    private val cooldownManager by get<CooldownManager>()

    @ExperimentalTime
    override fun GearyEntity.tick() {
        parent?.with<Player>() { player ->
            player.sendActionBar(cooldownManager.incompleteCooldowns.entries.joinToString("\n") { (key, cooldown) ->
                val displayLength = 10
                val displayChar = '■'
                val length = Duration.milliseconds(cooldown.length)
                val timeLeft = Duration.milliseconds((cooldown.endTime - System.currentTimeMillis()))
                val squaresLeft =
                    if (timeLeft.toDouble(DurationUnit.SECONDS) * 20 < INTERVAL) 0 else (timeLeft / length * displayLength).roundToInt()

                buildString {
                    append("$key ")
                    append(ChatColor.GREEN)
                    repeat(displayLength - squaresLeft) {
                        append(displayChar)
                    }
                    append(ChatColor.RED)
                    repeat(squaresLeft) {
                        append(displayChar)
                    }
                    append(ChatColor.GRAY)
                    if (squaresLeft < 1) (append(ChatColor.BOLD," ✓")) else append(" [$timeLeft]")
                }
            })
        }
    }
}
