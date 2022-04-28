package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.context.EngineContext
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.engine.Engine
import com.mineinabyss.geary.helpers.addParent
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.looty.debug
import org.koin.core.component.inject

class PlayerInstancedItems(
    internal val loadedEntities: MutableMap<PrefabKey, GearyEntity> = mutableMapOf()
) : Map<PrefabKey, GearyEntity> by loadedEntities, EngineContext {
    override val engine: Engine by inject()

    fun load(prefabKey: PrefabKey, parent: GearyEntity): GearyEntity {
        return loadedEntities.getOrPut(prefabKey) {
            entity {
                addPrefab(prefabKey.toEntity() ?: error("No prefab found for key"))
                addParent(parent)
                debug("Loaded prefab $prefabKey")
            }
        }
    }

    fun unload(element: PrefabKey): Boolean {
        return loadedEntities.remove(element)?.removeEntity() != null
    }

    fun unloadAll() {
        loadedEntities.values.forEach { it.removeEntity() }
        loadedEntities.clear()
    }
}
