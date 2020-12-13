package com.mineinabyss.looty

import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}
