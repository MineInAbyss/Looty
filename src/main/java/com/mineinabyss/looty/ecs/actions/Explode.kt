package com.mineinabyss.looty.ecs.actions

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.actions.GearyAction
import com.mineinabyss.geary.minecraft.actions.AtPlayerLocation
import com.mineinabyss.geary.minecraft.actions.ConfigurableLocation
import com.mineinabyss.idofront.spawning.spawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.TNTPrimed

@Serializable
@SerialName("looty:explode")
class Explode(
    private val at: ConfigurableLocation = AtPlayerLocation(),
    private val power: Float = 4F,
    private val setFire: Boolean = false,
    private val breakBlocks: Boolean = false,
    private val fuseTicks: Int = 0
) : GearyAction() {
    override fun runOn(entity: GearyEntity): Boolean {
        val location = at.get(entity) ?: return false

        if (fuseTicks <= 0)
            location.createExplosion(power, setFire, breakBlocks)
        else //only spawn a tnt in if we have a fuse
            location.spawn<TNTPrimed> {
                fuseTicks = this@Explode.fuseTicks
            }
        return true
    }
}
