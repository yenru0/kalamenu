package yenru0.yrkaier.kalamenu

import net.minecraft.server.v1_14_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack


open class KalaUtil {
    fun findKalaId(itemStack: ItemStack) : String? {
        val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getString("kalaId")
    }
    fun findKalaCost(itemStack: ItemStack) : Int? {
        val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getInt("kalaCost")
    }
    fun findKalaDurability(itemStack: ItemStack) : Int? {
        val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getInt("kalaDurability")
    }

    fun breakKalaDurability(itemStack: ItemStack, damage: Int) : ItemStack {
        val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
        tag.setInt("kalaDurability", findKalaDurability(itemStack)!!.minus(damage))
        nbt.tag = tag
        return CraftItemStack.asBukkitCopy(nbt)
    }
}