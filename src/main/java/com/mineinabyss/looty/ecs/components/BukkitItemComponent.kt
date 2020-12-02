package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import org.bukkit.inventory.ItemStack

class BukkitItemComponent(
        var itemStack: ItemStack,
        var slot: Int,
): GearyComponent()
