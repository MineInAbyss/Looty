package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.looty.config.looty
import org.bukkit.NamespacedKey

/**
 * This system is implemented separate from idofront recipes since they are handled differently by Minecraft.
 */
@AutoScan
fun GearyModule.createPotionMixRecipeSystem() = listener(
    object : ListenerQuery() {
        val prefabKey by get<PrefabKey>()
        val potionMixes by get<SetPotionMixes>()
        override fun ensure() = event.anySet(::potionMixes, ::prefabKey)
    }
).exec {
    val result = potionMixes.result?.toItemStackOrNull() ?: gearyItems.createItem(prefabKey)

    if (result != null) {
        potionMixes.potionmixes.forEachIndexed { i, potionmix ->
            val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")
            looty.plugin.server.potionBrewer.removePotionMix(key)
            looty.plugin.server.potionBrewer.addPotionMix(potionmix.toPotionMix(key, result))
        }
    } else looty.plugin.logger.warning("PotionMix $prefabKey is missing result item")
}
