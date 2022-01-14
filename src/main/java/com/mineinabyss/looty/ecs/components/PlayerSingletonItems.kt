package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.entities.addParent
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.looty.debug

//@Serializable
//@SerialName("looty:player_singleton_items")
class PlayerSingletonItems(
    internal val loadedEntities: MutableMap<PrefabKey, GearyEntity> = mutableMapOf()
) : Map<PrefabKey, GearyEntity> by loadedEntities {
    fun load(prefabKey: PrefabKey, parent: GearyEntity): GearyEntity {
        return loadedEntities.getOrPut(prefabKey) {
            Engine.entity {
                addPrefab(prefabKey.toEntity() ?: error("No prefab found for key"))
                addParent(parent)
                debug("Loaded prefab $prefabKey")
            }
        }
    }

    fun unload(element: PrefabKey): Boolean {
        return loadedEntities.remove(element)?.removeEntity() != null
    }

    fun clear() {
        loadedEntities.values.forEach { it.removeEntity() }
        loadedEntities.clear()
    }
}
