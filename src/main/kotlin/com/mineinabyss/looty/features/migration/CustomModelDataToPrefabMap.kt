package com.mineinabyss.looty.features.migration

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.looty.features.type.LootyTypeQuery
import org.bukkit.Material

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
