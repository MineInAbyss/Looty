package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.entities.addParent
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.tracking.gearyOrNull
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}

object LootyFactory {
    fun createFromPrefab(
        parent: GearyEntity,
        prefab: GearyEntity,
        context: PlayerInventoryContext,
    ): GearyEntity? {
        val (_, slot, inventory) = context
        val type = prefab.get<LootyType>() ?: return null

        return Engine.entity {
            addParent(parent)
            addPrefab(prefab)
            set(context)
            val uuid = setPersisting(UUID.randomUUID())

            val item = encodeComponentsTo(type)
            set(item)
            inventory.setItem(slot, item)

            BukkitAssociations.register(uuid, this)
        }
    }

    fun loadFromPlayerInventory(context: PlayerInventoryContext): GearyEntity? {
        val item = context.item ?: return null
        if (item.type == Material.AIR) return null
        val gearyPlayer = gearyOrNull(context.holder) ?: return null

        if(gearyOrNull(item)?.get<PlayerInventoryContext>()?.slot == context.slot) return null

        return Engine.entity {
            addParent(gearyPlayer)
            decodeComponentsFrom(item.itemMeta.persistentDataContainer)
            set(context)
            set(item)
            // Ensure a UUID is set and actually unique
            val uuid = get<UUID>()?.takeIf { it !in BukkitAssociations } ?: setPersisting(UUID.randomUUID())

            debug("Creating item in slot ${context.slot} and uuid $uuid")
            BukkitAssociations.register(uuid, this)
            encodeComponentsTo(item)
        }
    }
}

fun GearyEntity.encodeComponentsTo(lootyType: LootyType): ItemStack =
    lootyType.item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
