package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.idofront.plugin.listeners

interface ItemRecipes {
    val query: ItemRecipeQuery

    companion object : GearyAddonWithDefault<ItemRecipes> {
        val recipes by lazy { default().query.registerRecipes() }

        override fun default() = object : ItemRecipes {
            override val query = ItemRecipeQuery()
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
