package com.mineinabyss.looty


import com.mineinabyss.geary.minecraft.dsl.gearyAddon
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.idofront.slimjar.IdofrontSlimjar
import com.mineinabyss.looty.ecs.components.events.LootyEventListener
import com.mineinabyss.looty.ecs.systems.*
import com.mineinabyss.looty.ecs.systems.singletonitems.SingletonItemRemover
import kotlinx.serialization.InternalSerializationApi
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: Looty by lazy { JavaPlugin.getPlugin(Looty::class.java) }

@OptIn(InternalSerializationApi::class)
class Looty : JavaPlugin() {
    val itemsDir = File(dataFolder, "items")

    override fun onEnable() {
        IdofrontSlimjar.loadGlobally(this)
        saveDefaultConfig()
        reloadConfig()

        //Register commands
        LootyCommands()

        registerEvents(
            LootyEventListener,
            InventoryTrackingListener,
            LootyTypeItemUpdaterSystem,
        )

        registerService<SerializablePrefabItemService>(LootySerializablePrefabItemService)

        gearyAddon {
            systems(
                ItemTrackerSystem,
                ScreamingSystem,
                CooldownDisplaySystem,
                ItemRecipeSystem(),
                PlayerInventoryContextTracker(),
                HeldItemTracker(),
                PeriodicSaveSystem,
                SingletonItemRemover,
            )

            autoscanActions()
            autoscanComponents()

            bukkitEntityAssociations {
                onEntityRegister<Player> {
                    get<Player>()?.let { player -> ItemTrackerSystem.refresh(player) }
                }

                onEntityUnregister<Player> {
                    get<Player>()?.let { player -> ItemTrackerSystem.refresh(player) }
                }
            }
            loadPrefabs(itemsDir)
        }
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
//        Engine.forEach<ChildItemCache> { it.clear() }
    }
}
