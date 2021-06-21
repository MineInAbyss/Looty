package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.looty.dto.LootyEventNames
import com.mineinabyss.looty.ecs.components.*
import com.mineinabyss.looty.ecs.components.events.listeners.consumeItemOnBroke
import com.mineinabyss.looty.ecs.components.events.listeners.replaceItemToBroken
import com.mineinabyss.looty.events.LootyItemBrokeEvent
import com.mineinabyss.looty.tracking.gearyOrNull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import java.util.*

val Player.heldLootyItem get() = gearyOrNull(inventory.itemInMainHand)

object LootyEventListener : Listener {
    @EventHandler
    fun PlayerInteractEvent.onClick() {
        if (leftClicked) event(player.heldLootyItem, LootyEventNames.LEFT_CLICK)
        if (rightClicked) event(player.heldLootyItem, LootyEventNames.RIGHT_CLICK)
    }

    @EventHandler(ignoreCancelled = true)
    fun PlayerItemBreakEvent.onItemBreak() {
        event(player.heldLootyItem, LootyEventNames.BREAK)
    }

    //TODO dropping items reloads them in the tracking system even if cancelled
    @EventHandler(ignoreCancelled = true)
    fun PlayerDropItemEvent.onItemDrop() {
        event(player.heldLootyItem, LootyEventNames.DROP)
    }

    //TODO some of these will get repetitive between items and mobs, consider sharing code somehow
    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onHit() {
        val player = damager as? Player ?: return
        val gearyEntity = player.heldLootyItem ?: return

        gearyEntity.set(Target(geary(entity)))
        event(gearyEntity, LootyEventNames.HIT_ENTITY)
        gearyEntity.remove<Target>()
    }

    @EventHandler(ignoreCancelled = true)
    fun PlayerItemConsumeEvent.onConsume() {
        event(player.heldLootyItem, LootyEventNames.CONSUME)

    }

    @EventHandler(ignoreCancelled = true)
    fun LootyItemBrokeEvent.onBroke() {
        event(entity, LootyEventNames.LOOTY_ITEM_BROKE)
        consumeItemOnBroke(entity)
        replaceItemToBroken(entity)
    }
}
