package com.mineinabyss.looty.features.recipes

//import com.mineinabyss.geary.annotations.AutoScan
//import com.mineinabyss.geary.prefabs.PrefabKey
//import com.mineinabyss.geary.systems.RepeatingSystem
//import com.mineinabyss.geary.systems.accessors.TargetScope
//import com.mineinabyss.looty.looty
//import org.bukkit.NamespacedKey

// TODO migrate to item recipes in looty
//@AutoScan
//class PotionMixRecipeSystem : RepeatingSystem() {
//    private val TargetScope.prefabKey by get<PrefabKey>()
//    private val TargetScope.potionmixes by get<SetPotionMixes>()
//
//    override fun TargetScope.tick() {
//        val result = potionmixes.result?.toItemStackOrNull()
//            ?: LootyFactory.createFromPrefab(this.prefabKey)
//
//        if (result != null) {
//            potionmixes.potionmixes.forEachIndexed { i, potionmix ->
//                val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")
//                looty.server.potionBrewer.removePotionMix(key)
//                looty.server.potionBrewer.addPotionMix(potionmix.toPotionMix(key, result))
//            }
//            entity.remove<SetPotionMixes>()
//        } else looty.logger.warning("PotionMix $prefabKey is missing result item")
//    }
//}
