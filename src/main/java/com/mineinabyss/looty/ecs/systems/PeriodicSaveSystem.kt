package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PersistingComponents
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import org.bukkit.inventory.ItemStack

object PeriodicSaveSystem : TickingSystem(interval = 100) {
    private val QueryResult.persisting by get<PersistingComponents>()
    private val QueryResult.item by get<ItemStack>()

    override fun QueryResult.tick() {
        saveToItem(entity, persisting, item)
    }

    fun saveToItem(
        entity: GearyEntity,
        persisting: PersistingComponents,
        item: ItemStack
    ) {
        val thoroughEval = every(iterations = 100) { true } ?: false

        val oldHash = persisting.hashed
        val newHash = persisting.updateComponentHash()

        if (thoroughEval) {
            entity.encodeComponentsTo(item)
            return
        }

        if (newHash == oldHash) return

        entity.encodeComponentsTo(item)
    }
}
