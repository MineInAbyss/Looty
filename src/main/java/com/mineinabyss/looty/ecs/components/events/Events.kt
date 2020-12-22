package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.serialization.FlatSerializer
import com.mineinabyss.geary.ecs.serialization.FlatWrap
import com.mineinabyss.looty.ecs.components.events.actions.LootyAction
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = EventComponentSerializer::class)
data class Events(
        override val wrapped: Map<String, List<LootyAction>>
) : GearyComponent, FlatWrap<Map<String, List<LootyAction>>>

private object EventComponentSerializer : FlatSerializer<Events, Map<String, List<LootyAction>>>(
        "geary:events", serializer(), { Events(it) }
)
