package com.mineinabyss.looty.ecs.actions

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.actions.GearyAction
import com.mineinabyss.geary.ecs.components.parent
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.idofront.spawning.spawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.TNTPrimed

@Serializable
@SerialName("looty:explode")
class Explode : GearyAction() {
    override fun runOn(entity: GearyEntity) {
        entity.parent?.with<PlayerComponent> { (player) ->
            player.location.spawn<TNTPrimed>()
        }
    }
}
