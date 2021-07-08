package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > geary:player_instanced_item
 *
 * Indicates a Looty item entity should exist once per player instead of once for each ItemStack in the inventory.
 */
@Serializable
@SerialName("geary:player_instanced_item")
@AutoscanComponent
class PlayerInstancedItem
