package com.mineinabyss.looty.features.food

import com.mineinabyss.geary.papermc.tracking.items.toGeary
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.random.Random

class CustomFoodSystem : Listener {
    @EventHandler
    fun PlayerItemConsumeEvent.onConsumeFood() {
        val gearyInventory = player.inventory.toGeary() ?: return

        val entity = if (hand == EquipmentSlot.HAND)
            gearyInventory.itemInMainHand
        else gearyInventory.itemInOffhand ?: return

        val gearyFood = entity?.get<Food>() ?: return

        val replacement = gearyFood.replacement?.toItemStack()
        isCancelled = true // Cancel vanilla behaviour

        if (player.gameMode != GameMode.CREATIVE) {
            if (replacement != null) {
                if (player.inventory.firstEmpty() != -1) player.inventory.addItem(replacement)
                else player.world.dropItemNaturally(player.location, replacement)
            }
            item.subtract()

            if (gearyFood.effectList.isNotEmpty() && Random.nextDouble(0.0, 1.0) <= gearyFood.effectChance)
                player.addPotionEffects(gearyFood.effectList)
        }

        player.foodLevel += minOf(gearyFood.hunger, 20)
        player.saturation += minOf(gearyFood.saturation, 20)
    }
}
