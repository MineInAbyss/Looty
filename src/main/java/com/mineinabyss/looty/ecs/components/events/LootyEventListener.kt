package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.actions.CancelEventAction
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.looty.ecs.components.ChildItemCache
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent

val Player.heldLootyItem get() = get<ChildItemCache>()?.get(inventory.heldItemSlot)

fun Event.event(entity: GearyEntity?, name: String) {
    entity?.get<Events>()?.wrapped?.get(name)?.forEach {
        it.runOn(entity)
        if (it is CancelEventAction && this is Cancellable)
            isCancelled = true
    }
}

object LootyEventListener : Listener {
    @EventHandler
    fun PlayerInteractEvent.onClick() {
        if (leftClicked) event(player.heldLootyItem, "leftClick")
        if (rightClicked) event(player.heldLootyItem, "rightClick")
    }

    @EventHandler(ignoreCancelled = true)
    fun PlayerItemBreakEvent.onItemBreak() {
        event(player.heldLootyItem, "break")
    }

    //TODO dropping items reloads them in the tracking system even if cancelled
    @EventHandler(ignoreCancelled = true)
    fun PlayerDropItemEvent.onItemDrop() {
        event(player.heldLootyItem, "drop")
    }
}
