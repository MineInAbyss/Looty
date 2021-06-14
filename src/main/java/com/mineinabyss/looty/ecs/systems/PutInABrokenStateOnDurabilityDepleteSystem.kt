package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.PutInABrokenStateDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.UpdateContextItemComponent

object PutInABrokenStateOnDurabilityDepleteSystem : LootyItemSystem() {
    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()
    private val putInABrokenStateDurabilityDepleteComponent by get<PutInABrokenStateDurabilityDepleteComponent>()

    override fun GearyEntity.tick() {
        val entity = BukkitAssociations[uuid] ?: return
        val context = entity.get<PlayerInventoryContext>()
        context?.item?.let {
            if (!lootyType.item.type.isItem) return
            set(UpdateContextItemComponent(putInABrokenStateDurabilityDepleteComponent.item))
        }
    }
}
