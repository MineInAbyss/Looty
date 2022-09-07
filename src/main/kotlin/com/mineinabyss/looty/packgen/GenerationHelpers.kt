package com.mineinabyss.looty.packgen

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.toSerializable
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.LootyPackGen
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery.pack
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery.packKey
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta

interface GenerationHelpers {
    private val genValues: GenerationValues get() = GenerationValues()

    fun Material.generatePredicates(lootyItems: List<SerializableItemStack>): String {
        val json = JsonObject()
        json.addProperty("parent", this.getParentModel())
        json.add("textures", this.getTextures())
        json.add("overrides", this.getOverrides(lootyItems))

        when (this) {
            Material.BOW -> json.add("display", genValues.bowDisplay.toJsonObject())
            Material.CROSSBOW -> json.add("display", genValues.crossbowDisplay.toJsonObject())
            Material.SHIELD -> {
                json.addProperty("gui_light", "front")
                json.add("display", genValues.shieldDisplay.toJsonObject())
            }

            else -> {}
        }

        return json.toString()
    }

    //TODO Add more?
    private fun Material.getParentModel(): String {
        return when {
            this.isBlock -> "block/cube_all"
            genValues.tools.any { it.isTagged(this) } -> "item/handheld"
            this == Material.FISHING_ROD -> "item/handheld_rod"
            this == Material.SHIELD -> "builtin/entity"
            else -> "item/generated"
        }
    }

    private fun Material.getTextures(): JsonObject {
        val textures = JsonObject()
        val itemMeta = ItemStack(this).itemMeta

        textures.addProperty(
            "layer0",
            this.getVanillaTextureName(false) + if (itemMeta is PotionMeta) "_overlay" else ""
        )

        if (itemMeta is LeatherArmorMeta)
            textures.addProperty("layer1", this.getVanillaTextureName(false) + "_overlay")

        if (itemMeta is PotionMeta)
            textures.addProperty("layer1", this.getVanillaTextureName(false))

        return textures
    }

    private fun Material.getVanillaTextureName(isModel: Boolean): String {
        return when {
            this.isBlock -> "block/${this.toString().lowercase()}"
            !isModel && this == Material.CROSSBOW -> "item/crossbow_standby"
            else -> "item/${this.toString().lowercase()}"
        }
    }

    private fun Material.getOverrides(lootyItems: List<SerializableItemStack>): JsonArray {
        val overrides = JsonArray()

        when {
            this == Material.SHIELD -> overrides.add(getOverrides("blocking", 1, "item/shield_blocking"))
            this == Material.BOW -> {
                val pullingPredicate = JsonObject()
                pullingPredicate.addProperty("pulling", 1)
                overrides.add(getOverrides(pullingPredicate.toString().toJsonObject(), "item/bow_pulling_0"))
                pullingPredicate.addProperty("pull", 0.65)
                overrides.add(getOverrides(pullingPredicate.toString().toJsonObject(), "item/bow_pulling_1"))
                pullingPredicate.addProperty("pull", 0.9)
                overrides.add(getOverrides(pullingPredicate, "item/bow_pulling_2"))
            }

            this == Material.CROSSBOW -> {
                val pullingPredicate = JsonObject()
                pullingPredicate.addProperty("pulling", 1)
                overrides.add(getOverrides(pullingPredicate.toString().toJsonObject(), "item/crossbow_pulling_0"))
                pullingPredicate.addProperty("pull", 0.65)
                overrides.add(getOverrides(pullingPredicate.toString().toJsonObject(), "item/crossbow_pulling_1"))
                pullingPredicate.addProperty("pull", 0.9)
                overrides.add(getOverrides(pullingPredicate, "item/crossbow_pulling_2"))
            }
        }

        lootyItems.forEach { lootyItem ->
            overrides.add(getOverrides("custom_model_data", lootyItem.customModelData ?: 0, lootyItem.getModelPath()))
        }
        return overrides
    }

    private fun SerializableItemStack.getModelPath(): String {
        return LootyResourcepackQuery.firstOrNull { LootyFactory.createFromPrefab(it.packKey)?.toSerializable() == this }?.pack?.getModelPath() ?: ""
    }

    private fun String.toJsonObject(): JsonObject {
        return JsonParser.parseString(this).asJsonObject
    }

    private fun getOverrides(property: String, propertyValue: Int, model: String): JsonObject {
        return getOverrides(JsonObject(), property, propertyValue, model)
    }

    private fun getOverrides(predicate: JsonObject, property: String, propertyValue: Int, model: String): JsonObject {
        predicate.addProperty(property, propertyValue)
        return getOverrides(predicate, model)
    }

    private fun getOverrides(predicate: JsonObject, model: String): JsonObject {
        val override = JsonObject()
        override.add("predicate", predicate)
        override.addProperty("model", model)
        return override
    }
    fun LootyPackGen.getModelPath(): String {
        val modelPath = model.ifBlank { texture }
        val namespace = modelPath.substringBefore(":", "minecraft")
        val file = modelPath.substringAfter(":", modelPath)
        return "$namespace:$file"
    }
}
