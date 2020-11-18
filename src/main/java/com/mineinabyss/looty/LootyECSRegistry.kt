package com.mineinabyss.looty

import com.mineinabyss.geary.dsl.attachToGeary
import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.ecs.systems.PotionEffectSystem
import com.mineinabyss.looty.ecs.systems.ScreamingSystem
import kotlinx.serialization.modules.SerializersModule
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
    }
}
