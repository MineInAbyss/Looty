package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.ecs.components.Prefab
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : Query({
    has<LootyType>()
    has<Prefab>()
}) {
    val QueryResult.key by get<PrefabKey>()
}
