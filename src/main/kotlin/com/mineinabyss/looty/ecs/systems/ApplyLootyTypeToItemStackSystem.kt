package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.textcomponents.serialize
import com.mineinabyss.looty.LootyFactory.removeItalics
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.OriginalDisplayName
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import org.bukkit.inventory.ItemStack

/**
 * Updates an entity's ItemStack to the one specified in LootyType when it is first set.
 */
@AutoScan
class ApplyLootyTypeToItemStackSystem : GearyListener() {
    val TargetScope.item by onFirstSet<ItemStack>()
    val TargetScope.lootyType by onSet<LootyType>()

    @Handler
    fun TargetScope.updateItem() {
        val originalDisplayName = item.toGearyFromUUIDOrNull()?.get<OriginalDisplayName>()?.originalDisplayName
        val oldDisplayName = item.itemMeta?.displayName()

        val baseDisplayName = lootyType.item.apply { toItemStack(item) }.displayName?.removeItalics()


        item.editItemMeta {
            if (originalDisplayName != oldDisplayName?.serialize())
                displayName(oldDisplayName?.removeItalics())
            else displayName(baseDisplayName)
        }.toGearyFromUUIDOrNull()?.setPersisting(OriginalDisplayName(baseDisplayName?.serialize()))
    }
}
