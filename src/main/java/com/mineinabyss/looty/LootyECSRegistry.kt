package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.ecs.systems.PotionEffectSystem
import com.mineinabyss.looty.ecs.systems.ScreamingSystem
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object LootyECSRegistry {
    fun register() {
        registerSystems()
        registerComponentSerialization()
    }

    private fun registerSystems() {
        Engine.addSystems(
                ItemTrackerSystem,
                ScreamingSystem,
                PotionEffectSystem
        )
    }

    private fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        Formats.addSerializerModule(SerializersModule {
            polymorphic(GearyComponent::class) {
                subclass(Screaming.serializer())
                subclass(PotionComponent.serializer())
            }
        })
    }
}
