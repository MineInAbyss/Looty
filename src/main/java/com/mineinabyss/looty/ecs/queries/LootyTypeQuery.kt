package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : Query() {
    val QueryResult.key by get<PrefabKey>()
    val QueryResult.type by get<LootyType>()
}