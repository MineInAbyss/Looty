package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.ComponentAddSystem
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

object LootyTypeItemUpdaterSystem : ComponentAddSystem() {
    val GearyEntity.item by get<ItemStack>()
    val GearyEntity.lootyType by get<LootyType>()

    override fun GearyEntity.run() {
        lootyType.item.toItemStack(item)
    }
}
