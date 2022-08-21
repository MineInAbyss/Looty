package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.RegisterPotionMixComponent

import com.mineinabyss.looty.looty
import org.bukkit.NamespacedKey

@AutoScan
class PotionMixRecipeSystem : RepeatingSystem() {
    private val TargetScope.prefabKey by get<PrefabKey>()
    private val TargetScope.potionmixes by get<RegisterPotionMixComponent>()
    val TargetScope.isPrefab by family { has<Prefab>() }

    override fun TargetScope.tick() {
        val result = potionmixes.result?.toItemStack() ?: LootyFactory.createFromPrefab(prefabKey)
        potionmixes.potionmixes.forEachIndexed { i, potionmix ->
            val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")

            if (result == null) {
                looty.logger.warning("PotionMix $key is missing result item")
                return@forEachIndexed
            }
            looty.server.potionBrewer.addPotionMix(potionmix.toPotionMix(key, result))
        }
    }
}
