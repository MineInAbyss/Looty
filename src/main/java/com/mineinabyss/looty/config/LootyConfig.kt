package com.mineinabyss.looty.config

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.looty.looty
import com.okkero.skedule.schedule
import kotlinx.serialization.Serializable


object LootyConfig : IdofrontConfig<LootyConfig.Data>(looty, Data.serializer()) {
    @Serializable
    class Data(
            val debug: Boolean = false
    )

    private val addons = mutableListOf<LootyAddon>()

    fun registerAddon(addon: LootyAddon) = addons.add(addon)

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        looty.schedule {
            waitFor(1)
            activateAddons()
        }
    }

    override fun reload(): ReloadScope.() -> Unit = {
        logSuccess("Unregistering types in Looty config")
        LootyTypes.reset()

        attempt("Reactivated all addons", "Failed to reactivate addons") {
            activateAddons()
        }
    }

    //TODO put something like this into geary ecs registration and share with mobzy
    private fun activateAddons() {
        LootyTypes.reset()
        for (addon in addons) {
            addon.relicsDir.walk().filter { it.isFile }.forEach { file ->
                val name = file.nameWithoutExtension
                try {
                    val type = Formats.yamlFormat.decodeFromString(LootyType.serializer(), file.readText())
                    LootyTypes.registerType(name, type) //TODO namespaces
                } catch (e: Exception) {
                    logError("Error deserializing item: $name")
                    e.printStackTrace()
                }
            }
        }
    }
}
