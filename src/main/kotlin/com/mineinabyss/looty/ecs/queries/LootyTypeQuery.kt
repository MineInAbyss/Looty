package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : Query() {
    override fun onStart() {
        has<LootyType>()
        has<Prefab>()
    }

    val TargetScope.key by get<PrefabKey>()
}
