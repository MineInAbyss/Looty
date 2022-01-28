package com.mineinabyss.looty.ecs.components

import org.bukkit.inventory.ItemStack

/**
 * A class that stores an item with components properly encoded that was picked up by players in creative mode.
 *
 * When the player goes to put it back, the data gets copied over. We can't do this when the item is picked up because
 * clients basically have full control over creative inventory.
 */
class PickedUpItemData(
    val item: ItemStack
)
