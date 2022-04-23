package com.mineinabyss.looty

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.meta.ItemMeta

@Serializable
@SerialName("looty:item")
object LootySerializablePrefabItemService : SerializablePrefabItemService, GearyMCContext by GearyMCContextKoin() {
    override fun encodeFromPrefab(item: ItemStack, meta: ItemMeta, prefabName: String) {
        LootyFactory.updateItemFromPrefab(item, meta, PrefabKey.of(prefabName)) //TODO encode
    }
}

data class LootyRecipeChoice(
    val item: ItemStack
) : RecipeChoice, GearyMCContext by GearyMCContextKoin() {
    val prefabs = item.itemMeta.persistentDataContainer.decodePrefabs()

    override fun test(itemStack: ItemStack): Boolean {
        return itemStack.isSimilar(item) || itemStack.itemMeta.persistentDataContainer.decodePrefabs()
            .containsAll(prefabs)
    }

    override fun clone(): RecipeChoice = copy()

    override fun getItemStack(): ItemStack = item
}
