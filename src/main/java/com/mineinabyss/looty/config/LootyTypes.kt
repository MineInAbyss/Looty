package com.mineinabyss.looty.config

import com.mineinabyss.geary.ecs.types.GearyEntityTypes
import com.mineinabyss.looty.looty
import org.bukkit.inventory.ItemStack

object LootyTypes : GearyEntityTypes<LootyType>(looty) {
    operator fun get(item: ItemStack): LootyType = TODO("Getting type from item not yet implemented")
}
