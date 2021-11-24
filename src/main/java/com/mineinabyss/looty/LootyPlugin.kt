package com.mineinabyss.looty


import com.mineinabyss.geary.minecraft.dsl.gearyAddon
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.idofront.slimjar.IdofrontSlimjar
import com.mineinabyss.looty.ecs.systems.*
import com.mineinabyss.looty.ecs.systems.singletonitems.SingletonItemRemover
import kotlinx.serialization.InternalSerializationApi
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: LootyPlugin by lazy { JavaPlugin.getPlugin(LootyPlugin::class.java) }

@OptIn(InternalSerializationApi::class)
class LootyPlugin : JavaPlugin() {
    val itemsDir = File(dataFolder, "items")

    override fun onEnable() {
        IdofrontSlimjar.loadToLibraryLoader(this)
        saveDefaultConfig()
        reloadConfig()

        //Register commands
        LootyCommands()

        registerEvents(
            InventoryTrackingListener,
        )

        registerService<SerializablePrefabItemService>(LootySerializablePrefabItemService)

        gearyAddon {
            systems(
                ItemTrackerSystem,
                LootyTypeItemUpdaterSystem,
                ItemRecipeSystem(),
                PlayerInventoryContextTracker(),
                HeldItemTracker(),
                PeriodicSaveSystem,
                SingletonItemRemover,
            )

            autoscanAll()

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
