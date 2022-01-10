package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.Handler
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

@AutoScan
class LootyTypeItemUpdaterSystem : GearyListener() {
    val TargetScope.item by get<ItemStack>()
    val TargetScope.lootyType by get<LootyType>()

    init {
        allAdded()
    }

    @Handler
    fun TargetScope.updateItem() {
        lootyType.item.toItemStack(item)
    }
}
