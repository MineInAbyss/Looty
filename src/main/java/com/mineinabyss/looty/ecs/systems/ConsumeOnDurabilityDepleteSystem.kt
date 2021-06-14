package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.looty.ecs.components.ConsumeOnDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext

object ConsumeOnDurabilityDepleteSystem : LootyItemSystem() {
    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()

    override fun GearyEntity.tick() {
        if (!has<ConsumeOnDurabilityDepleteComponent>()) return
        val entity = BukkitAssociations[uuid] ?: return
        val context = entity.get<PlayerInventoryContext>()
        context?.item?.apply { context.inventory.remove(this) }
    }
}
