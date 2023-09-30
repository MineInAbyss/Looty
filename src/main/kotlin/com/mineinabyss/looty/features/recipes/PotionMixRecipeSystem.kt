package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.looty.config.looty
import org.bukkit.NamespacedKey

/**
 * This system is implemented separate from idofront recipes since they are handled differently by Minecraft.
 */
class PotionMixRecipeSystem : GearyListener() {
    private val Pointers.prefabKey by get<PrefabKey>().whenSetOnTarget()
    private val Pointers.potionmixes by get<SetPotionMixes>().whenSetOnTarget()

    override fun Pointers.handle() {
        val result = potionmixes.result?.toItemStackOrNull() ?: gearyItems.createItem(prefabKey)

        if (result != null) {
            potionmixes.potionmixes.forEachIndexed { i, potionmix ->
                val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")
                looty.plugin.server.potionBrewer.removePotionMix(key)
                looty.plugin.server.potionBrewer.addPotionMix(potionmix.toPotionMix(key, result))
            }
        } else looty.plugin.logger.warning("PotionMix $prefabKey is missing result item")
    }
}
