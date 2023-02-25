package com.mineinabyss.looty.features.wearables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Sound

/**
 * `geary:hat`
 * Lets an item be worn as a hat.
 *
 * @param sound The sound to play when equipped.
 */
@Serializable
@SerialName("geary:hat")
class Hat(
    val sound: Sound = Sound.ITEM_ARMOR_EQUIP_NETHERITE
)
