package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

@AutoScan
class LootyTypeItemUpdaterSystem : GearyListener() {
    val TargetScope.item by added<ItemStack>()
    val TargetScope.lootyType by added<LootyType>()

    @Handler
    fun TargetScope.updateItem() {
        lootyType.item.toItemStack(item)
    }
}
