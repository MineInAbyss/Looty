package com.mineinabyss.looty.config

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.StaticType
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.store.encodeComponents
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.looty.ecs.components.LootyEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LootyType(
        val item: SerializableItemStack,
        private val _name: String? = null,
        private val _staticComponents: MutableSet<GearyComponent> = mutableSetOf(),
        private val _components: Set<GearyComponent> = setOf(),
) : GearyEntityType() {
    @Transient
    override val types = LootyTypes

    fun instantiateItemStack() = item.toItemStack().editItemMeta {
        persistentDataContainer.encodeComponents(instantiateComponents() + StaticType("Looty", name))
    }

    override fun instantiate(): LootyEntity = //TODO should we even instantiate all this stuff right away?
            TODO("Unsure if it should even be possible to instantiate an item entity from here")
//            LootyEntity(Engine.getNextId(), instantiateItemStack()).apply {
//                addComponent(StaticType("Looty", name))
//                addComponents(instantiateComponents())
//            }
}
