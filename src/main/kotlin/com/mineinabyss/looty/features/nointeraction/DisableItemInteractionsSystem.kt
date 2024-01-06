package com.mineinabyss.looty.features.nointeraction

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

@AutoScan
class DisableItemInteractionsSystem : GearyListener() {
    private val Pointers.bukkit by get<Event>().map { (it as? Cancellable) }.on(event)
//    private val Pointers.interacted by family { has<Interacted>() }.on(event)
    private val Pointers.noVanilla by family { has<DisableItemInteractions>() }.on(target)

    override fun Pointers.handle() {
        bukkit?.isCancelled = true
    }
}

