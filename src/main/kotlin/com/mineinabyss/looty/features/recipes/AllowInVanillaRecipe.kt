package com.mineinabyss.looty.features.recipes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component to indicate that an ItemStack should be allowed in vanilla crafting recipes.
 * Meaning if a GearyItem has a base-material of PAPER and this component, it can be used to craft books.
 */
@Serializable
@SerialName("looty:allow_in_vanilla_recipes")
class AllowInVanillaRecipes
