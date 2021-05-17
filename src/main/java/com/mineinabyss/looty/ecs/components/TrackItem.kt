package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import java.util.*

data class TrackItem(
    val uuid: UUID,
    val index: Int,
    val inventory: PlayerInventory,
)
