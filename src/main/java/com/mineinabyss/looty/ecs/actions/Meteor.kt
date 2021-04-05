package com.mineinabyss.looty.ecs.actions

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.actions.AtPlayerLocation
import com.mineinabyss.geary.minecraft.actions.ConfigurableLocation
import com.mineinabyss.geary.minecraft.events.Events
import com.mineinabyss.idofront.spawning.spawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.SizedFireball
import org.bukkit.util.Vector

//TODO this is really unnecessary once we have a proper entity spawn action
// It's just something I was messing around with.
/**
 * Spawns a fireball above a given location which flies down towards it.
 *
 * @param at The location to spawn the fireball at.
 * @param events An events component to attach to the fireball.
 */
@Serializable
@SerialName("looty:meteor")
class Meteor(
    private val at: ConfigurableLocation = AtPlayerLocation(),
    private val events: Events
) : GearyAction() {
    override fun runOn(entity: GearyEntity): Boolean {
        val location = at.get(entity) ?: return false

        val spawnLoc = location.add(0.0, 20.0, 0.0)
        spawnLoc.spawn<SizedFireball>() {
            direction = Vector(0, -1, 0)
            geary(this) {
                set(events)
            }
        }
        return true
    }
}
