package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.onComponentAdd
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

object LootyTypeItemUpdaterSystem : GearyListener() {
    val ResultScope.item by get<ItemStack>()
    val ResultScope.lootyType by get<LootyType>()

    override fun GearyHandlerScope.register() {
        onComponentAdd {
            lootyType.item.toItemStack(item)
        }
    }
}
