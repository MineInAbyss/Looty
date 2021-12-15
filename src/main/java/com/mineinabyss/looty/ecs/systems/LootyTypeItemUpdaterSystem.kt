package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.ComponentAddHandler
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

@AutoScan
class LootyTypeItemUpdaterSystem : GearyListener() {
    val ResultScope.item by get<ItemStack>()
    val ResultScope.lootyType by get<LootyType>()

    private inner class UpdateItem : ComponentAddHandler() {
        override fun ResultScope.handle(event: EventResultScope) {
            lootyType.item.toItemStack(item)
        }
    }
}
