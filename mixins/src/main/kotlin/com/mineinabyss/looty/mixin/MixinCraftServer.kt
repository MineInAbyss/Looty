package com.mineinabyss.looty.mixin

import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.logging.Logger


@Mixin(CraftServer::class, remap = false)
abstract class MixinCraftServer {
    @get:Shadow
    abstract val logger: Logger

    @Inject(method = ["<init>"], at = [At("RETURN")])
    private fun onConstruction(callback: CallbackInfo) {
        logger.info("Hello World!")
    }
}
