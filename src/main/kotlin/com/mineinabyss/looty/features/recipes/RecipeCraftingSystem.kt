package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.datastore.decodePrefabs
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent

class RecipeCraftingSystem : Listener {
    /**
     * Prevents custom items being usable in vanilla recipes based on their material,
     * when they have a [DenyInVanillaRecipes] component, by setting result to null.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun PrepareItemCraftEvent.onCraftWithCustomItem() {
        // Ensure this only cancels vanilla recipes
        if (recipe == null || (recipe as? Keyed)?.key()?.namespace() != "minecraft") return

        if (inventory.matrix.any {
                it?.itemMeta?.persistentDataContainer
                    ?.decodePrefabs()
                    ?.firstOrNull()
                    ?.toEntityOrNull()
                    ?.has<DenyInVanillaRecipes>() == true
            }) {
            inventory.result = null
        }
    }
}
