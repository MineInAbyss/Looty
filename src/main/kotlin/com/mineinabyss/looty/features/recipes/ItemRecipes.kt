package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.systems.builders.cachedQuery
import com.mineinabyss.geary.systems.query.CachedQueryRunner
import com.mineinabyss.idofront.plugin.listeners

interface ItemRecipes {
    val query: CachedQueryRunner<ItemRecipeQuery>

    companion object : GearyAddonWithDefault<ItemRecipes> {
        val recipes by lazy { default().query.registerRecipes() }

        override fun default() = object : ItemRecipes {
            override val query = geary.cachedQuery(ItemRecipeQuery())
        }

        override fun ItemRecipes.install() {
            geary.pipeline.runOnOrAfter(GearyPhase.ENABLE) {
                val autoDiscoveredRecipes = recipes

                gearyPaper.plugin.listeners(
                    RecipeDiscoverySystem(autoDiscoveredRecipes),
                    RecipeCraftingSystem(),
                )
            }
        }
    }
}
