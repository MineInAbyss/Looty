package com.mineinabyss.looty.ecs.components.events.actions

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.MaxDurabilityComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.inventory.ItemStack

private var durabilityLoreMessage: String = ""
private var regexPattern: Regex = Regex("")
private val replaceRegex = Regex("\\$\\d+")

fun updateDurabilityLoreMessage()
{
    durabilityLoreMessage = LootyConfig.data.durabilityLoreMessage
    regexPattern = Regex(durabilityLoreMessage.replace(replaceRegex) { "\\d+" })
}

fun GearyEntity.updateDurabilityLore()
{
    get<PlayerInventoryContext>()?.apply {
        val durability = get<DurabilityComponent>()?.durability ?: return
        val maxDurability = get<MaxDurabilityComponent>()?.maxDurability ?: return
        item?.setDurability(durability, maxDurability)
    }
}

private fun ItemStack.setDurability(durability: Int, maxDurability: Int)
{
    val lore = lore() ?: ArrayList<Component>()
    lore.removeDurability()
    lore.addDurability(durability, maxDurability)
    lore(lore)
}

private fun MutableList<Component>.removeDurability() =
    filterIsInstance<TextComponent>()
        .filter { it.content().matches(regexPattern) }
        .forEach { remove(it) }

private fun MutableList<Component>.addDurability(durability: Int, maxDurability: Int)
    = createDurabilityComponent(durability, maxDurability).also { add(it) }

private fun createDurabilityComponent(durability: Int, maxDurability: Int): TextComponent
    = createDurabilityString(durability, maxDurability).let { Component.text(it) }

private fun createDurabilityString(durability: Int, maxDurability: Int): String
    = durabilityLoreMessage
        .replace("$1", durability.toString())
        .replace("$2", maxDurability.toString())

