package com.mineinabyss.looty.features.migration

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.looty.initializers.LootyType
import org.bukkit.inventory.ItemStack

/**
 * Updates an entity's ItemStack to the one specified in LootyType when it is first set.
 */
@AutoScan
class ApplyLootyTypeToItemStackSystem : GearyListener() {
    val TargetScope.item by onFirstSet<ItemStack>()
    val TargetScope.lootyType by onSet<LootyType>()

    @Handler
    fun TargetScope.updateItem() {
        lootyType.item.toItemStack(item)
    }
}
