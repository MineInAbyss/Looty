package com.mineinabyss.looty.mixin

import com.mineinabyss.looty.interfaces.IPlayerTest
import net.minecraft.server.v1_16_R3.PlayerInventory
import org.bukkit.Bukkit
import org.spongepowered.asm.mixin.Mixin


@Mixin(PlayerInventory::class)
abstract class MixinPlayerInventory : IPlayerTest {
    override fun test() {
        println("Test")
        Bukkit.broadcastMessage("test")
    }


//    @Inject(method = ["add"], at = [At("HEAD")])
//    fun addItem(i: Int, itemstack: ItemStack, ci: CallbackInfoReturnable<Boolean>) {
//        Bukkit.broadcastMessage("Added $itemstack at $i")
//    }
//
//    @Inject(method = ["a"], at = [At("HEAD")])
//    fun placeItemBackInInventory(x: Int, y: Int, ci: CallbackInfo) {
//        Bukkit.broadcastMessage("Placed back at $x, $y")
//    }
//
//
//    @Inject(method = ["splitStack"], at = [At("HEAD")])
//    fun removeItem(x: Int, y: Int, ci: CallbackInfoReturnable<ItemStack>) {
//        Bukkit.broadcastMessage("Removed at $x, $y")
//    }
//
//    @Inject(method = ["f"], at = [At("HEAD")])
//    fun removeItem(itemStack: ItemStack, ci: CallbackInfo) {
//        Bukkit.broadcastMessage("Removed $itemStack")
//    }
//
//    @Inject(method = ["splitWithoutUpdate"], at = [At("HEAD")])
//    fun splitWithoutUpdate(i: Int, ci: CallbackInfoReturnable<ItemStack>) {
//        Bukkit.broadcastMessage("Split without update at $i")
//    }
//
//    @Inject(method = ["setItem"], at = [At("HEAD")])
//    fun setItem(slot: Int, itemStack: ItemStack, ci: CallbackInfo) {
//        Bukkit.broadcastMessage("Set $itemStack at $slot")
//    }
}
