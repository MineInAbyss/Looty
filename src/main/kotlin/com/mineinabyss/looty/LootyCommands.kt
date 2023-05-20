package com.mineinabyss.looty

import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.looty.config.looty

class LootyCommands : IdofrontCommandExecutor() {
    override val commands = commands(looty.plugin) {
        "looty" {
            "reload" {
                action {
                    looty.configController.reload()
                }
            }
        }
    }
}
