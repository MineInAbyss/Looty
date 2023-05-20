package com.mineinabyss.looty

import com.mineinabyss.geary.papermc.tracking.items.itemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import org.bukkit.inventory.ItemStack

class LootySerializablePrefabItemService : SerializablePrefabItemService {
    override fun encodeFromPrefab(item: ItemStack, prefabName: String) {
        itemTracking.provider.serializePrefabToItemStack(PrefabKey.of(prefabName), existing = item)
    }
}
