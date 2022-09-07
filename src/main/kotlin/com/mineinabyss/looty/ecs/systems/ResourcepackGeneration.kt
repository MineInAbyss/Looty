package com.mineinabyss.looty.ecs.systems

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.messaging.logVal
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.toSerializable
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.LootyPackGen
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery.pack
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery.packKey
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery.type
import com.mineinabyss.looty.looty
import okio.Path.Companion.toPath
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import java.io.File
import java.nio.charset.Charset

class ResourcepackGeneration {
    private val genValues = GenerationValues()
    fun generateDefaultAssets() {
        LootyTypeQuery.mapNotNull { it.type.item.type }.forEach { material ->
            val root = "${looty.dataFolder.absolutePath}/assets/minecraft/models/item"
            root.toPath().toFile().mkdirs()
            //TODO True in most but not all cases
            val materialFile = File("$root/${material.toString().lowercase()}.json")
            val lootyItems = LootyTypeQuery.run { filter { it.type.item.type == material }.map { it.type.item } }
            materialFile.createNewFile()
            materialFile.writeText(material.generatePredicates(lootyItems), Charset.defaultCharset())
        }
    }

    private fun Material.generatePredicates(lootyItems: List<SerializableItemStack>): String {
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
        return LootyResourcepackQuery.firstOrNull { it.packKey.getSerializableItemStack() == this }?.pack?.getModelPath()
            ?: ""
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

    fun generateLootyItemAssets() {
        LootyResourcepackQuery.forEach {
            val generateModel: Boolean = it.pack.model.isBlank()
            val (namespace, filePath) = it.pack.getModelPath().logVal("modelpath: ").split(":")

            if (generateModel) {
                val json = JsonObject()
                val textures = JsonObject()

                json.addProperty("parent", "item/generated")
                textures.addProperty("layer0", "${namespace}:$filePath")
                json.add("textures", textures)

                val modelFile =
                    "${looty.dataFolder.absolutePath}/assets/$namespace/models/$filePath.json".toPath().toFile()
                modelFile.parentFile.mkdirs()
                modelFile.createNewFile()
                modelFile.writeText(json.toString(), Charset.defaultCharset())
            }
        }
    }

    private fun LootyPackGen.getModelPath(): String {
        val modelPath = model.ifBlank { texture }
        val namespace = modelPath.substringBefore(":", "minecraft")
        val file = modelPath.substringAfter(":", modelPath)
        return "$namespace:$file"
    }

    private fun PrefabKey.getSerializableItemStack(): SerializableItemStack? {
        return LootyFactory.createFromPrefab(this)?.toSerializable()
    }
}

open class GenerationValues(
    val shieldDisplay: String = """{
        thirdperson_righthandd
        :{
        rotation
        :[0,90,0],
        translation
        :[10,6,-4],
        scale
        :[1,1,1]},
        thirdperson_lefthand
        :{
        rotation
        :[0,90,0],
        translation
        :[10,6,12],
        scale
        :[1,1,1]},
        firstperson_righthand
        :{
        rotation
        :[0,180,5],
        translation
        :[-10,2,-10],
        scale
        :[1.25,1.25,1.25]},
        firstperson_lefthand
        :{
        rotation
        :[0,180,5],
        translation
        :[10,0,-10],
        scale
        :[1.25,1.25,1.25]},
        gui
        :{
        rotation
        :[15,-25,-5],
        translation
        :[2,3,0],
        scale
        :[0.65,0.65,0.65]},
        fixed
        :{
        rotation
        :[0,180,0],
        translation
        :[-2,4,-5],
        scale
        :[0.5,0.5,0.5]},
        ground
        :{
        rotation
        :[0,0,0],
        translation
        :[4,4,2],
        scale
        :[0.25,0.25,0.25]}}""".trimIndent(),
    val bowDisplay: String = """{
        thirdperson_righthand
        :{
        rotation
        :[-80,260,-40],
        translation
        :[-1,-2,2.5],
        scale
        :[0.9,0.9,0.9]},
        thirdperson_lefthand
        :{
        rotation
        :[-80,-280,40],
        translation
        :[-1,-2,2.5],
        scale
        :[0.9,0.9,0.9]},
        firstperson_righthand
        :{
        rotation
        :[0,-90,25],
        translation
        :[1.13,3.2,1.13],
        scale
        :[0.68,0.68,0.68]},
        firstperson_lefthand
        :{
        rotation
        :[0,90,-25],
        translation
        :[1.13,3.2,1.13],
        scale
        :[0.68,0.68,0.68]}}""".trimIndent(),
    val crossbowDisplay: String = """{
        thirdperson_righthand
        :{
        rotation
        :[-90,0,-60],
        translation
        :[2,0.1,-3],
        scale
        :[0.9,0.9,0.9]},
        thirdperson_lefthand
        :{
        rotation
        :[-90,0,30],
        translation
        :[2,0.1,-3],
        scale
        :[0.9,0.9,0.9]},
        firstperson_righthand
        :{
        rotation
        :[-90,0,-55],
        translation
        :[1.13,3.2,1.13],
        scale
        :[0.68,0.68,0.68]},
        firstperson_lefthand
        :{
        rotation
        :[-90,0,35],
        translation
        :[1.13,3.2,1.13],
        scale
        :[0.68,0.68,0.68]}}""".trimIndent(),
    val tools: Set<MaterialSetTag> = setOf(
        MaterialTags.PICKAXES,
        MaterialTags.AXES,
        MaterialTags.SHOVELS,
        MaterialTags.HOES,
        MaterialTags.SWORDS
    )

)
