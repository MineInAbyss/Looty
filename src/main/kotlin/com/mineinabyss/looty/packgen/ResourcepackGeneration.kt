package com.mineinabyss.looty.packgen

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import com.google.gson.JsonObject
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery
import com.mineinabyss.looty.ecs.queries.LootyResourcepackQuery.pack
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery.type
import com.mineinabyss.looty.looty
import okio.Path.Companion.toPath
import java.io.File
import java.nio.charset.Charset

class ResourcepackGeneration : GenerationHelpers {
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

    fun generateLootyItemAssets() {
        LootyResourcepackQuery.forEach {
            val generateModel: Boolean = it.pack.model.isBlank()
            val (namespace, filePath) = it.pack.getModelPath().split(":")

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
