package com.mineinabyss.looty.features.holdsentity

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:holds_prefab")
class HoldsEntity(
    val prefabKey: PrefabKey,
    val emptiedItem: SerializableItemStack? = null
)
