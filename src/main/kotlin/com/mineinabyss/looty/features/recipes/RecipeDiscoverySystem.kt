package com.mineinabyss.looty.features.recipes

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RecipeDiscoverySystem(
    val discoveredRecipes: List<NamespacedKey>
) : Listener {
    @EventHandler
    fun PlayerJoinEvent.showRecipesOnJoin() {
        player.discoverRecipes(discoveredRecipes)
    }
}
