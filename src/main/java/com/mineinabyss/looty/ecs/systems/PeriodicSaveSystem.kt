package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PersistingComponents
import com.mineinabyss.geary.minecraft.store.GearyStore

class PeriodicSaveSystem : TickingSystem(interval = 100) {
    private val persisting by get<PersistingComponents>()
    //    private val persistingPrefabs by getOrNull<PersistingPrefabs>()

    override fun GearyEntity.tick() {
        val thoroughEval = every(iterations = 100) { true } ?: false

        val oldHash = persisting.hashed
        val newHash = persisting.components.hashCode()
        persisting.hashed = newHash

        // When thoroughly evaluating, encode values and check if they are equal to those previously encoded
        if (thoroughEval) {
            val encoded = GearyStore.encode(this)
            if (
                newHash != oldHash || // We know for sure if hashes don't match, contents are identical
                !GearyStore.read(this).contentEquals(encoded)
            ) GearyStore.write(this, encoded)
            return
        }

        if (newHash == oldHash) return

        GearyStore.write(this)
    }
}
