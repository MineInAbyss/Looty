package com.mineinabyss.looty.features.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.recipes.PotionMixRecipeIngredients
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:potion_mixes")
class SetPotionMixes(
    val result: SerializableItemStack? = null,
    val potionmixes: List<PotionMixRecipeIngredients> = emptyList(),
)
