package com.mineinabyss.looty.features.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.recipes.SerializableRecipeIngredients
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:recipes")
class RegisterRecipeComponent(
    val recipes: List<SerializableRecipeIngredients>,
    val discoverRecipes: Boolean = false,
    val group: String = "",
    val removeRecipes: List<String> = listOf(),
    val result: SerializableItemStack? = null,
)
