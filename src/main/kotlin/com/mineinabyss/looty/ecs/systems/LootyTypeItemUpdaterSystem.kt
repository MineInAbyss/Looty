package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

@AutoScan
class LootyTypeItemUpdaterSystem : GearyListener() {
    val TargetScope.item by added<ItemStack>()
    val TargetScope.lootyType by added<LootyType>()

    @Handler
    fun TargetScope.updateItem() {
        lootyType.item.updateMeta(item, item.itemMeta)
    }
}
