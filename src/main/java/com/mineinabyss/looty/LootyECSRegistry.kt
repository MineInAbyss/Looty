package com.mineinabyss.looty

import com.mineinabyss.geary.dsl.attachToGeary
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.looty.ecs.components.events.Events
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.ecs.systems.PotionEffectSystem
import com.mineinabyss.looty.ecs.systems.ScreamingSystem

fun Looty.attachToGeary() {
    attachToGeary(types = LootyTypes) {
        systems(
                ItemTrackerSystem,
                ScreamingSystem,
                PotionEffectSystem,
        )

        components {
            component(Screaming.serializer())
            component(PotionComponent.serializer())
            component(Events.serializer())
        }

        bukkitEntityAccess {
            onPlayerRegister { player ->
                add(ChildItemCache(player))
            }
            onPlayerUnregister { gearyPlayer, player ->
                gearyPlayer.with<ChildItemCache> {
                    it.reevaluate(player.inventory)
                    it.clear()
                }
            }
        }
    }
}
