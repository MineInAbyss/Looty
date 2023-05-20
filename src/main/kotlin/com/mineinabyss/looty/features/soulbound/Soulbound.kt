package com.mineinabyss.geary.features.soulbound

import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Prevents an item from dropping on death when held by its [owner].
 */
@Serializable
@SerialName("looty:soulbound")
class Soulbound(
    var owner: @Serializable(with = UUIDSerializer::class) UUID,
)
