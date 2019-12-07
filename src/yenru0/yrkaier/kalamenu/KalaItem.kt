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
    var kalaCost : Int = 0
        set(v: Int) {
            val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemstack)
            val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
            tag.setInt("kalaCost", v)
            nbt.tag = tag
            this.itemstack = CraftItemStack.asBukkitCopy(nbt)
            field = v
        }

    var kalaDurability : Int? = null
        set(v: Int?) {
            if (v != null){
                val nbt : net.minecraft.server.v1_14_R1.ItemStack = CraftItemStack.asNMSCopy(itemstack)
                val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
                tag.setInt("kalaDurability", v)
                nbt.tag = tag
                this.itemstack = CraftItemStack.asBukkitCopy(nbt)
                field = v
            }
        }

    constructor() {

    }
    constructor(type: Material, kalaId: String, kalaCost: Int, name: String, lore: List<String>?) {
        itemstack = ItemStack(type)
        val itemmeta : ItemMeta = itemstack.itemMeta!!
        itemmeta.setDisplayName(name)
        itemmeta.lore = lore
        itemstack.itemMeta = itemmeta

        this.kalaId = kalaId // essential
        this.kalaCost = kalaCost
    }

    constructor(type: Material, kalaId: String, kalaCost: Int, kalaDurability: Int, name: String, lore: List<String>?) {
        itemstack = ItemStack(type)
        val itemmeta : ItemMeta = itemstack.itemMeta!!
        itemmeta.setDisplayName(name)
        itemmeta.lore = lore
        itemstack.itemMeta = itemmeta

        this.kalaId = kalaId // essential
        this.kalaCost = kalaCost
        this.kalaDurability = kalaDurability
    }






}

enum class KalaItems (val v : KalaItem) {
    Bible(KalaItem(Material.BOOK, "bible_normal", 16, "기본 성경책",
        listOf(
            "양갱을 위한 기본적인 성격책이다.",
            "우클릭을 하면 약간 체력을 약간 회복하고 소모된다."
        )
    )),
    BibleR(KalaItem(Material.ENCHANTED_BOOK, "bible_reinforced", 36, 3, "강화된 성경책",
        listOf(
            "양갱을 위한 강화된 성경책이다.",
            "우클릭을 하면 약간 체력을 회복하고 3번 쓸 수 있다.",
            "가성비 있다. 겹쳐 쓰지 않는 걸 추천 하지 않는다."
        )
    ))
    ;

}