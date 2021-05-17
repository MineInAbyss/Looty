package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.TrackItem
import com.mineinabyss.looty.tracking.lootyUUID
import com.mineinabyss.looty.tracking.toNMS

object ItemTracker : TickingSystem() {
    private val trackedItem by get<TrackItem>()

    override fun GearyEntity.tick() {
        val item = trackedItem.inventory.getItem(trackedItem.index)
        if(item == null || item.toNMS().lootyUUID != trackedItem.uuid) {
            removeEntity()
            return
        }
        set(item)
    }
}
