package com.mineinabyss.looty.ecs.components.itemcontexts

import com.mineinabyss.geary.papermc.store.hasComponentsEncoded
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ProcessingItemContext(
    private val item: ItemStack,
) {
    val meta: ItemMeta by lazy { item.itemMeta }
    val gearyItem by lazy { meta.toGearyFromUUIDOrNull() }

    fun updateMeta(): ItemStack {
        item.itemMeta = meta
        return item
    }

    val hasComponentsEncoded get() = meta.persistentDataContainer.hasComponentsEncoded
}

inline fun ItemStack.useWithLooty(run: ProcessingItemContext.() -> Unit) {
    if (!hasItemMeta()) return
    with(ProcessingItemContext(this)) {
        run()
    }
}
