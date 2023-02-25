package com.mineinabyss.looty.features.nointeraction

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.commons.components.interaction.Interacted
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.papermc.components.NoVanillaInteractions
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.SourceScope
import com.mineinabyss.geary.systems.accessors.building.map
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

@AutoScan
class NoItemInteractionsSystem : GearyListener() {
    private val EventScope.bukkit by get<Event>().map { it as? Cancellable }
    private val EventScope.interacted by family { has<Interacted>() }
    private val SourceScope.noVanilla by family { has<NoVanillaInteractions>() }

    @Handler
    fun onPlace(event: EventScope) {
        event.bukkit?.isCancelled = true
    }
}
