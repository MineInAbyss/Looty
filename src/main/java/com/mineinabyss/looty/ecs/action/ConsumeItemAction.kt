package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:consume_item")
@AutoscanComponent
class ConsumeItemAction(val no_param: Boolean = true) : GearyAction() {
    private val GearyEntity.context by get<PlayerInventoryContext>()

    override fun GearyEntity.run(): Boolean {
        context.item?.apply { context.inventory.remove(this) }
        Engine.removeEntity(this.id)
        return true;
    }
}
