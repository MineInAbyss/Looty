package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.looty.ecs.components.ChildItemCache
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent

val Player.heldLootyItem get() = get<ChildItemCache>()?.get(inventory.heldItemSlot)

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

    //TODO some of these will get repetitive between items and mobs, consider sharing code somehow
    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onHit() {
        val player = damager as? Player ?: return
        val gearyEntity = player.heldLootyItem ?: return

        gearyEntity.addComponent(Target(geary(entity)))
        event(gearyEntity, "hitEntity")
        gearyEntity.removeComponent<Target>()
    }
}
