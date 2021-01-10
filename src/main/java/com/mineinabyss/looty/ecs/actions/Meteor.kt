package com.mineinabyss.looty.ecs.actions

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.actions.GearyAction
import com.mineinabyss.geary.minecraft.actions.AtPlayerLocation
import com.mineinabyss.geary.minecraft.actions.ConfigurableLocation
import com.mineinabyss.idofront.spawning.spawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.SizedFireball
import org.bukkit.util.Vector

@Serializable
@SerialName("looty:meteor")
class Meteor(
    private val at: ConfigurableLocation = AtPlayerLocation()
) : GearyAction() {
    override fun runOn(entity: GearyEntity): Boolean {
        val location = at.get(entity) ?: return false

        val spawnLoc = location.add(0.0, 20.0, 0.0)
        spawnLoc.spawn<SizedFireball>() {
            direction = Vector(0, -1, 0)
        }
        return true
    }
}
