package com.mineinabyss.looty.mixin

import net.minecraft.server.v1_16_R3.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ItemStack::class)
abstract class MixinItemStack {
    @Inject(method = ["<init>*"], at = [At("RETURN")])
    private fun onConstruction(callback: CallbackInfo) {
        println("ItemStack created ${(this as ItemStack)}")
    }
}
