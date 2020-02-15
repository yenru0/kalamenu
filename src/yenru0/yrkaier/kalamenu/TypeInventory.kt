package yenru0.yrkaier.kalamenu

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


class TypeInventory : InventoryHolder {

    enum class TypeId {
        CCHEST, SELL, TRADE, BUY,
        FORGE, REINFORCE
    }

    lateinit var inven: Inventory
    final var type: TypeId? = null

    constructor(inventory: Inventory, type: TypeId?) {
        this.inven = inven
        this.type = type
    }

    constructor(size: Int, name: String, type: TypeId?) {
        this.inven = Bukkit.createInventory(this, size, name)
        this.type = type
    }

    constructor(name: String = "null", type: TypeId?) {
        this.type = type
        when (type) {
            TypeId.CCHEST-> {
                this.inven = Bukkit.createInventory(this, 27, name)
            }
            TypeId.TRADE -> {

            }
            TypeId.SELL -> {
                this.inven = Bukkit.createInventory(this, 18, "sell gui")
            }
            TypeId.BUY -> {
                fun getCostLine(kalacost: Int) = run {
                    val its: ItemStack = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
                    val itm = its.itemMeta!!
                    itm.setDisplayName("${ChatColor.GREEN}${kalacost}r-exp(xpcoin)")
                    its.setItemMeta(itm)
                    its
                }
                this.inven = Bukkit.createInventory(this, 18, "buy gui")
                this.inven.setItem(0, KalaItems.ScrollTeleportWeak.v.itemstack)
                this.inven.setItem(0+9, getCostLine(KalaItems.ScrollTeleportWeak.v.kalaCost))
                this.inven.setItem(1, KalaItems.ScrollTeleportNormal.v.itemstack)
                this.inven.setItem(1+9, getCostLine(KalaItems.ScrollTeleportNormal.v.kalaCost))
                this.inven.setItem(2, KalaItems.ScrollTeleportStrong.v.itemstack)
                this.inven.setItem(2+9, getCostLine(KalaItems.ScrollTeleportStrong.v.kalaCost))
                this.inven.setItem(3, KalaItems.DriverNormal.v.itemstack)
                this.inven.setItem(3+9, getCostLine(KalaItems.DriverNormal.v.kalaCost))
                this.inven.setItem(4, KalaItems.ShieldStone.v.itemstack)
                this.inven.setItem(4+9, getCostLine(KalaItems.ShieldStone.v.kalaCost))
            }

            TypeId.REINFORCE -> {
                this.inven = Bukkit.createInventory(this, 54, "Reinforce")

                val leftlines = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
                val leftlines_im = leftlines.itemMeta
                leftlines_im!!.setDisplayName("${ChatColor.BLUE}피강화 아이템 슬롯")
                leftlines_im.setCustomModelData(7000)
                leftlines.itemMeta = leftlines_im

                val rightlines = ItemStack(Material.RED_STAINED_GLASS_PANE)
                val rightlines_im = rightlines.itemMeta
                rightlines_im!!.setDisplayName("${ChatColor.RED}재물 아이템 슬롯")
                rightlines_im.setCustomModelData(7000)
                rightlines.itemMeta = rightlines_im

                val midlines = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
                val midlines_im = midlines.itemMeta
                midlines_im!!.setDisplayName("${ChatColor.GREEN}강화 시도하기")
                midlines_im.setCustomModelData(7000)
                midlines.itemMeta = midlines_im


                for(i in listOf(0, 1, 2, 3, 9, 18, 27, 36, 45, 46, 47, 48) ){
                    this.inven.setItem(i, leftlines)
                }
                for(i in listOf(5, 6, 7, 8, 17, 26, 35, 44, 53, 52, 51, 50)){
                    this.inven.setItem(i, rightlines)
                }
                for(i in listOf(4, 13, 22, 31, 40, 49)){
                    this.inven.setItem(i, midlines)
                }

            }
            TypeId.FORGE -> {

            }
        }
    }

    override fun getInventory(): Inventory {
        return this.inven
    }

}
