package com.mineinabyss.looty.ecs.components.events

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.events.call
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.dto.LootyEventNames
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.MaxDurabilityComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.events.actions.updateDurabilityBar
import com.mineinabyss.looty.ecs.components.events.actions.updateDurabilityLore
import com.mineinabyss.looty.events.LootyItemBrokeEvent
import com.mineinabyss.looty.events.LootyItemDurabilityChangedEvent
import com.mineinabyss.looty.events.LootyItemRepairedEvent
import com.mineinabyss.looty.tracking.gearyOrNull
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack

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
    }

    @EventHandler(ignoreCancelled = true)
    fun LootyItemRepairedEvent.onRepair() {
        event(entity, LootyEventNames.LOOTY_ITEM_REPAIRED)
    }

    @EventHandler(ignoreCancelled = true)
    fun LootyItemDurabilityChangedEvent.onDurabilityChanges() {
        updateDurabilityLore(entity)
        updateDurabilityBar(entity)
        event(entity, LootyEventNames.LOOTY_ITEM_DURABILITY_CHANGED)
    }
}
