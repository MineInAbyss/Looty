package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.serialization.FlatSerializer
import com.mineinabyss.geary.ecs.serialization.FlatWrap
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.SerializableRecipeIngredients
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

@Serializable(with = RegisterRecipeComponentSerializer::class)
@AutoscanComponent
class RegisterRecipeComponent(
    override val wrapped: List<SerializableDiscoverableRecipeIngredients>
) : FlatWrap<List<SerializableDiscoverableRecipeIngredients>>

object RegisterRecipeComponentSerializer :
    FlatSerializer<RegisterRecipeComponent, List<SerializableDiscoverableRecipeIngredients>>(
        "looty:recipes", serializer(), { RegisterRecipeComponent(it) }
    )

/**
 * could extend SerializableRecipeIngredients if it was an open class
 *
 * @see SerializableRecipeIngredients
 */
@Serializable
class SerializableDiscoverableRecipeIngredients(
    val items: Map<String, SerializableItemStack>,
    val configuration: String,
    val discoverRecipe: Boolean = false
) {
    fun toCraftingRecipe(key: NamespacedKey, result: ItemStack): Recipe {
        val recipe = ShapedRecipe(key, result)

        recipe.shape(*configuration.replace("|", "").split("\n").toTypedArray())

        items.forEach { (key, ingredient) ->
            recipe.setIngredient(key[0], RecipeChoice.ExactChoice(ingredient.toItemStack()))
        }

        return recipe
    }
}