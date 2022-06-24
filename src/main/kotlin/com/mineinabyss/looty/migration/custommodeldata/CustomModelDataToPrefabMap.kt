package com.mineinabyss.looty.migration.custommodeldata

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import org.bukkit.Material

data class CustomItem(
    val material: Material,
    val customModelData: Int
)

/**
 * Assists in migrating old items which only have custom model data to define their item type.
 */
object CustomModelDataToPrefabMap {
    private val map = mutableMapOf<CustomItem, PrefabKey>()

    init {
        LootyTypeQuery.run {
            forEach {
                val item = it.type.item
                map[CustomItem(item.type ?: return@forEach, item.customModelData ?: return@forEach)] = it.key
            }
        }
    }

    operator fun get(item: CustomItem) = map[item]
}
