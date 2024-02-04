package com.mineinabyss.looty

import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit

class LootyCommands : IdofrontCommandExecutor() {
    override val commands = commands(looty.plugin) {
        "looty" {
            "reload" {
                action {
                    looty.configController.reload()
                    Bukkit.updateRecipes()
                    sender.sendMessage("Reloaded Looty config")
                }
            }
        }
    }
}
