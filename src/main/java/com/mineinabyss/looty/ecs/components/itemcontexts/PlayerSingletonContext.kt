package com.mineinabyss.looty.ecs.components.itemcontexts

import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

class PlayerSingletonContext(
    val holder: Player,
    val itemSlots: MutableSet<Int> = mutableSetOf(),
    val inventory: PlayerInventory = holder.inventory,
)
