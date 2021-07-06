package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.LootyType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:restore_item_visual")
@AutoscanComponent
class RestoreItemVisualAction(val no_param: Boolean = true) : GearyAction() {
    private val GearyEntity.lootyType by get<LootyType>()

    override fun GearyEntity.run(): Boolean {
        ChangeItemVisualAction(lootyType.item).runOn(this)
        return true;
    }
}
