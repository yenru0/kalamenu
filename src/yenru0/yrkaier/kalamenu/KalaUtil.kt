package yenru0.yrkaier.kalamenu

import com.google.common.math.IntMath
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import net.minecraft.server.v1_15_R1.ItemStack as nbtItemStack
import net.minecraft.server.v1_15_R1.Entity as nbtEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import yenru0.yrkaier.kalamenu.Main
import kotlin.math.round

open class KalaUtil {

    companion object {
        //var assembly_table = listOf()
    }


    // kalaItems
    fun findKalaId(itemStack: ItemStack) : String? {
        val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getString("kalaId")
    }
    fun findKalaCost(itemStack: ItemStack) : Int? {
        val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getInt("kalaCost")
    }
    fun findKalaDurability(itemStack: ItemStack) : Int? {
        val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: return null
        return tag.getInt("kalaDurability")
    }

    fun breakKalaDurability(itemStack: ItemStack, damage: Int) : ItemStack {
        val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemStack)
        val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
        tag.setInt("kalaDurability", findKalaDurability(itemStack)!!.minus(damage))
        nbt.tag = tag
        return CraftItemStack.asBukkitCopy(nbt)
    }


    fun reinforcement(left: List<ItemStack?>, right: List<ItemStack?>, player: Player): List<ItemStack>?{
        var leftList: List<ItemStack> = left.mapNotNull {
            if (it != null){
                var tempid = it
                tempid
            } else {
                it
            }

        }

        var rightList: List<ItemStack> = right.mapNotNull {
            if (it != null){
                it
            } else {
                it
            }

        }

        if (rightList.size == 0) {
            return leftList + rightList
        }

        if (leftList.size != 1) {
            return leftList + rightList
        }
        var reinforced = leftList.first()
        var enchants: HashMap<Enchantment, Int> = hashMapOf()
        var rate: Double = 1.0
        var consumeExp = 125.0
        var item = 0


        rightList.forEach {

            var tempid = findKalaId(it) ?: "material:${it.type.toString()}"
            when(tempid){
                "item:book_enchant_sharpness6_yanganged" -> {
                    consumeExp += 1000
                    rate *= 0.75
                    enchants.put(Enchantment.DAMAGE_ALL, 6)

                }

                "item:book_enchant_knockback3_yanganged" -> {
                    consumeExp += 750
                    rate *= 0.8
                    enchants.put(Enchantment.KNOCKBACK, 3)
                }

                "item:book_enchant_sharpness5_normal" -> {
                    consumeExp += 500
                    rate *= 0.55
                    enchants.put(Enchantment.DAMAGE_ALL, 5)
                }

                "item:book_enchant_sharpness6_normal" -> {
                    consumeExp += 650
                    rate *= 0.50
                    enchants.put(Enchantment.DAMAGE_ALL, 6)
                }

                "item:book_enchant_fortune3_normal" -> {
                    consumeExp += 400
                    rate *= 0.50
                    enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 3)
                }

                "item:book_enchant_fortune4_normal" -> {
                    consumeExp += 700
                    rate *= 0.45
                    enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 4)
                }

                "item:book_enchant_fortune4_yanganged" -> {
                    consumeExp += 1200
                    rate *= 0.70
                    enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 4)
                }

                "item:book_enchant_fortune5_normal" -> {
                    consumeExp += 1000
                    rate *= 0.40
                    enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 5)
                }

                "item:book_enchant_loot4_normal" -> {
                    consumeExp += 700
                    rate *= 0.45
                    enchants.put(Enchantment.LOOT_BONUS_MOBS, 4)
                }

                "item:book_enchant_loot3_normal" -> {
                    consumeExp += 400
                    rate *= 0.50
                    enchants.put(Enchantment.LOOT_BONUS_MOBS, 3)
                }

                "item:driver_normal" -> {
                    consumeExp += 100
                    rate += 0.05
                }



                else -> {
                    consumeExp += 100
                    rate *= 0.68

                }


            }
        }

        var rnds = (1..1000).random()

        if (Main.expCoinsMap[player.uniqueId.toString()]!! < consumeExp){
            player.sendMessage("${ChatColor.RED}잉여 경험치가 부족합니다. (${Main.expCoinsMap[player.uniqueId.toString()]!!}/${consumeExp})")
            player.playSound(player.location, Sound.ENTITY_VILLAGER_AMBIENT, 3.0F, 0.5F)
            return leftList + rightList
        }

        if(reinforced.itemMeta is Damageable) {
            var temp_im = reinforced.itemMeta as Damageable
            temp_im.damage = 0
            reinforced.setItemMeta(temp_im as ItemMeta)
        }
        Main.expCoinsMap[player.uniqueId.toString()] = Main.expCoinsMap[player.uniqueId.toString()]!!.minus(consumeExp.toInt())
        if(rnds <= 1000*rate){
            for((e, v) in enchants){
                reinforced.addUnsafeEnchantment(e, v)
            }
            player.sendMessage("${ChatColor.GOLD}강화 성공")
            player.playSound(player.location, Sound.BLOCK_ANVIL_PLACE, 3.0F, 0.5F)
        } else {

            player.sendMessage("${ChatColor.RED}강화 실패")
            player.playSound(player.location, Sound.BLOCK_ANVIL_BREAK, 3.0F, 0.5F)
            return null
        }


        return listOf(reinforced)

    }



}

data class AssemblyCell(val recipeCount: HashMap<String, Int>){

}