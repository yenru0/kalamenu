package yenru0.yrkaier.kalamenu

import net.minecraft.server.v1_14_R1.NBTTagCompound
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta




class KalaItem : KalaUtil {
    lateinit var itemstack : ItemStack
    var kalaId : String = ""
        set(v: String) {
            val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemstack)
            val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
            tag.setString("kalaId", v)
            nbt.tag = tag
            this.itemstack = CraftItemStack.asBukkitCopy(nbt)
            field = v
        }

    constructor() {

    }
    constructor(type: Material, kalaId: String, name: String, lore: List<String>? = null) {
        itemstack = ItemStack(type)
        val itemmeta : ItemMeta = itemstack.itemMeta!!
        itemmeta.setDisplayName(name)
        itemmeta.lore = lore
        itemstack.itemMeta = itemmeta

        this.kalaId = kalaId // essential
    }






}

enum class KalaItems (val v : KalaItem) {
    Bible(KalaItem(Material.BOOK, "bible_normal", "기본 성경책", listOf("양갱을 위한 기본적인 성격책이다.", "우클릭을 하면 약간 체력을 약간 회복하고 소모된다."))),
    ;

}