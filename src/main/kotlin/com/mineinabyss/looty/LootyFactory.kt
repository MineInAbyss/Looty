package com.mineinabyss.looty

import com.mineinabyss.geary.datatypes.GearyEntityType
import com.mineinabyss.geary.papermc.datastore.encodeComponents
import com.mineinabyss.geary.papermc.datastore.encodePrefabs
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.looty.features.migration.CustomItem
import com.mineinabyss.looty.features.migration.CustomModelDataToPrefabMap
import com.mineinabyss.looty.initializers.LootyType
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer

class LootyFactory {
    /** Creates an ItemStack from a [prefabKey], encoding relevant information to it. */
    fun createFromPrefab(
        player: Player,
        prefabKey: PrefabKey,
    ): ItemStack? {
        val item = ItemStack(Material.AIR)
        updateItemFromPrefab(item, prefabKey)
        return item.takeIf { it.type != Material.AIR }
    }

    fun updateItemFromPrefab(item: ItemStack, prefabKey: PrefabKey) {
        val prefab = prefabKey.toEntityOrNull() ?: return
        prefab.get<LootyType>()?.item?.toItemStack(item)
        item.editMeta {
            it.persistentDataContainer.encodePrefabs(listOf(prefabKey))
        }
    }

    private fun updateOldLootyItem(prefabs: Set<PrefabKey>, item: NMSItemStack) {
        val tag = item.tag ?: return
        if (!tag.contains("CustomModelData")) return
        if (prefabs.isEmpty()) {
            val prefab = CustomModelDataToPrefabMap[CustomItem(
                CraftMagicNumbers.getMaterial(item.item),
                tag.getInt("CustomModelData")
            )] ?: return
            pdc.encodeComponents(setOf(), GearyEntityType())
            pdc.encodePrefabs(listOf(prefab))
        }
    }
}
