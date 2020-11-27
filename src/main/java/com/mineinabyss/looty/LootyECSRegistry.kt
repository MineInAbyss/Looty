package com.mineinabyss.looty

import com.mineinabyss.geary.dsl.attachToGeary
import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem.updateAndSaveItems
import com.mineinabyss.looty.ecs.systems.PotionEffectSystem
import com.mineinabyss.looty.ecs.systems.ScreamingSystem
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun Looty.attachToGeary() {
    attachToGeary(types = LootyTypes) {
        systems(
                ItemTrackerSystem,
                ScreamingSystem,
                PotionEffectSystem,
        )

        serializers {
            polymorphic(GearyComponent::class) {
                subclass(Screaming.serializer())
                subclass(PotionComponent.serializer())
            }
        }

        bukkitEntityAccess {
            onPlayerRegister {
                add(ChildItemCache())
            }
            onPlayerUnregister { gearyPlayer, player ->
                gearyPlayer.with<ChildItemCache> {
                    it.updateAndSaveItems(player.inventory, gearyPlayer)
                    it.clear()
                }
            }
        }
    }
}
