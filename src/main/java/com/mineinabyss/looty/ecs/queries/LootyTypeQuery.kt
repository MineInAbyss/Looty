package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.components.Prefab
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : Query() {
    init {
        has<LootyType>()
        has<Prefab>()
    }

    val TargetScope.key by get<PrefabKey>()
}
