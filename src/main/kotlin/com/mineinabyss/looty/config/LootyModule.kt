package com.mineinabyss.looty.config

import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.looty.LootyPlugin

val looty by DI.observe<LootyModule>()

class LootyModule(
    val plugin: LootyPlugin
) {
    val configController = config<LootyConfig>("config", plugin.dataFolder.toPath(), LootyConfig())
    val config: LootyConfig by configController
}
