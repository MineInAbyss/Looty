package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.datastore.decodePrefabs
import com.mineinabyss.geary.papermc.tracking.items.components.SetItem
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
        if (inventory.matrix
                .asSequence()
                .mapNotNull { it?.itemMeta?.persistentDataContainer?.decodePrefabs()?.firstOrNull()?.toEntityOrNull() }
                .any { it.has<SetItem>() && it.has<DenyInVanillaRecipes>() }
        ) {
            inventory.result = null
        }
    }
}
