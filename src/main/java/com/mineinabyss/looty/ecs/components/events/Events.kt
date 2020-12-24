package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.actions.GearyAction
import com.mineinabyss.geary.ecs.serialization.FlatSerializer
import com.mineinabyss.geary.ecs.serialization.FlatWrap
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = EventComponentSerializer::class)
data class Events(
        override val wrapped: Map<String, List<GearyAction>>
) : GearyComponent, FlatWrap<Map<String, List<GearyAction>>>

private object EventComponentSerializer : FlatSerializer<Events, Map<String, List<GearyAction>>>(
        "geary:events", serializer(), { Events(it) }
)
