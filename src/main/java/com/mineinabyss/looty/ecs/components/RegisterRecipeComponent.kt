package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.recipes.SerializableRecipeIngredients
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:recipes")
@AutoscanComponent
class RegisterRecipeComponent(
    val recipes: List<SerializableRecipeIngredients>,
    val discoverRecipes: Boolean = false,
    val group: String = "",
    val removeRecipes: List<String> = listOf()
)