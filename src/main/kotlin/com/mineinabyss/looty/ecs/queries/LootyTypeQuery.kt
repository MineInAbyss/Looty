package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.looty.ecs.components.LootyPackGen
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : GearyQuery() {
    val TargetScope.key by get<PrefabKey>()
    val TargetScope.type by get<LootyType>()
    val TargetScope.isPrefab by family { has<Prefab>() }
}

object LootyResourcepackQuery : GearyQuery() {
    val TargetScope.packKey by get<PrefabKey>()
    val TargetScope.pack by get<LootyPackGen>()
    val TargetScope.isResourcepack by family { has<Prefab>() }
}
