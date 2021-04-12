package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.serialization.FlatSerializer
import com.mineinabyss.geary.ecs.serialization.FlatWrap
import com.mineinabyss.idofront.serialization.SerializableRecipeIngredients
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = RegisterRecipeComponentSerializer::class)
@AutoscanComponent
class RegisterRecipeComponent(
    override val wrapped: List<SerializableRecipeIngredients>
) : FlatWrap<List<SerializableRecipeIngredients>>

object RegisterRecipeComponentSerializer :
    FlatSerializer<RegisterRecipeComponent, List<SerializableRecipeIngredients>>(
        "looty:recipes", serializer(), { RegisterRecipeComponent(it) }
    )
