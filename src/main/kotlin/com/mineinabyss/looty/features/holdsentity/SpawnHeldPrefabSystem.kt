package com.mineinabyss.looty.features.holdsentity

import com.mineinabyss.geary.papermc.tracking.entities.helpers.spawnFromPrefab
import com.mineinabyss.geary.papermc.tracking.items.toGeary
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class SpawnHeldPrefabSystem : Listener {
    @EventHandler(ignoreCancelled = true) // Fires after the onPickupMob thus it places it aswell
    fun PlayerInteractEvent.onEmptyMobzyBucket() {
        if (action != Action.RIGHT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return
        val heldEntity = player.inventory.toGeary()?.itemInMainHand?.get<HoldsEntity>() ?: return
        val block = clickedBlock?.getRelative(blockFace) ?: return

        block.location.toCenterLocation().spawnFromPrefab(heldEntity.prefabKey)
        player.inventory.setItemInMainHand(heldEntity.emptiedItem?.toItemStack())
        isCancelled = true // Cancel vanilla behaviour
    }
}
