package yenru0.yrkaier.kalamenu

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


class TypeInventory : InventoryHolder {

    enum class TypeId{
        CCHEST, SELL, TRADE
    }

    lateinit var inven: Inventory
    final var type: TypeId? = null

    constructor(inventory: Inventory, type: TypeId?){
        this.inven = inven
        this.type = type
    }

    constructor(size: Int, name: String, type: TypeId?){
        this.inven = Bukkit.createInventory(this, size, name)
        this.type = type
    }

    constructor(name:String = "null", type: TypeId?) {
        when(type) {
            TypeId.CCHEST, TypeId.TRADE -> {
                this.inven = Bukkit.createInventory(this, 27, name)
            }
            TypeId.SELL -> {
                this.inven = Bukkit.createInventory(this, 18, "sell gui")
            }
        }
    }

    override fun getInventory(): Inventory {
        return this.inven
    }

}
