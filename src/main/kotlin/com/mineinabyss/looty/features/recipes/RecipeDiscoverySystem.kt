package com.mineinabyss.looty.features.recipes

import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RecipeDiscoverySystem(
    val discoveredRecipes: Set<NamespacedKey>
) : Listener {
    @EventHandler
    fun PlayerJoinEvent.showRecipesOnJoin() {
        player.discoverRecipes(discoveredRecipes)
        if (looty.config.autoDiscoverVanillaRecipes)
            player.discoverRecipes(Bukkit.recipeIterator().asSequence().filterIsInstance<Keyed>()
                .filter { it.key.namespace == "minecraft" && it.key !in discoveredRecipes }.map { it.key }.toList())
    }
}
