package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.looty.ecs.components.ChildItemCache
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent

val Player.heldLootyItem get() = get<ChildItemCache>()?.get(inventory.heldItemSlot)

internal fun GearyEntity.event(name: String) {
    get<Events>()?.wrapped?.get(name)?.forEach {
        it.runOn(this)
    }
}

object LootyEventListener : Listener {
    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val (player) = e
        if (e.leftClicked) player.heldLootyItem?.event("leftClick")
        if (e.rightClicked) player.heldLootyItem?.event("rightClick")
    }

    @EventHandler
    fun onItemBreak(e: PlayerItemBreakEvent) {
        e.player.heldLootyItem?.event("break")
    }
}
