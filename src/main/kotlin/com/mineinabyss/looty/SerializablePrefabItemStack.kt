package com.mineinabyss.looty

import com.mineinabyss.geary.papermc.tracking.items.itemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import org.bukkit.inventory.ItemStack

class LootySerializablePrefabItemService : SerializablePrefabItemService {
    override fun encodeFromPrefab(item: ItemStack, prefabName: String) {
        val result = itemTracking.provider.serializePrefabToItemStack(PrefabKey.of(prefabName), existing = item)
        require(result != null) { "Failed to create serializable ItemStack from $prefabName, does the prefab exist and have a geary:set.item component?" }
    }
}
