package com.mineinabyss.looty.ecs.queries

import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.has
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.get
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.looty.ecs.components.LootyType

object LootyTypeQuery : GearyQuery() {
    val TargetScope.key by get<PrefabKey>()
    val TargetScope.isLooty by family {
        has<LootyType>()
        has<Prefab>()
    }
}
