package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("looty:consume_item")
@AutoscanComponent
class ConsumeItemAction(val no_param: Boolean = true) : GearyAction() {

    override fun GearyEntity.run(): Boolean {
        val item = get<ItemStack>() ?: return false
        item.amount = 0

        get<PlayerInventoryContext>()?.let {
            it.inventory.clear(it.slot)
        }

        Engine.removeEntity(id)
        return true
    }
}
