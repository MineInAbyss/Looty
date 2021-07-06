package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.minecraft.events.GearyMinecraftLoadEvent
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object LootyTypeItemUpdaterSystem : Listener {
    @EventHandler
    fun GearyMinecraftLoadEvent.onItemCreated() {
        val item = entity.get<ItemStack>() ?: return
        val lootyType = entity.get<LootyType>() ?: return
        lootyType.item.toItemStack(item)
    }
}
