package yenru0.yrkaier.kalamenu

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.SoundCategory
import org.bukkit.entity.Fireball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.metadata.FixedMetadataValue
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

class KalaItemEvent : KalaUtil(), Listener {
    @EventHandler
    fun whenUseKalaItem(e: PlayerInteractEvent){
        val player = e.player
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {

            if (e.item != null) {
                when (findKalaId(e.item!!)){
                    KalaItems.ScrollTeleportWeak.v.kalaId -> {
                        if (useScrollOfTeleport(player.location, Main.settpMap[player.uniqueId.toString()]!!, 1728)) {
                            player.sendMessage("${ChatColor.GOLD}이동됩니다.")
                            player.teleport(Main.settpMap[player.uniqueId.toString()]!!)

                        } else {
                            player.sendMessage("${ChatColor.RED}거리가 너무 멀어요!")
                        }
                        // consume
                        if (e.hand == EquipmentSlot.HAND) {
                            player.inventory.itemInMainHand.amount -= 1
                        }
                        else if (e.hand == EquipmentSlot.OFF_HAND) {
                            player.inventory.itemInOffHand.amount -= 1
                        }
                    }
                    KalaItems.ScrollTeleportNormal.v.kalaId -> {
                        if( useScrollOfTeleport(player.location, Main.settpMap[player.uniqueId.toString()]!!, 5832 ) ){
                            player.sendMessage("${ChatColor.GOLD}이동됩니다.")
                            player.teleport(Main.settpMap[player.uniqueId.toString()]!!)
                        } else {
                            player.sendMessage("${ChatColor.RED}거리가 너무 멀어요!")
                        }
                        if (e.hand == EquipmentSlot.HAND) {
                            player.inventory.itemInMainHand.amount -= 1
                        }
                        else if (e.hand == EquipmentSlot.OFF_HAND) {
                            player.inventory.itemInOffHand.amount -= 1
                        }
                    }
                    KalaItems.ScrollTeleportStrong.v.kalaId -> {
                        if( useScrollOfTeleport(player.location, Main.settpMap[player.uniqueId.toString()]!!, 10648 ) ){
                            player.sendMessage("${ChatColor.GOLD}이동됩니다.")
                            player.teleport(Main.settpMap[player.uniqueId.toString()]!!)
                        } else {
                            player.sendMessage("${ChatColor.RED}거리가 너무 멀어요!")
                        }
                        if (e.hand == EquipmentSlot.HAND) {
                            player.inventory.itemInMainHand.amount -= 1
                        }
                        else if (e.hand == EquipmentSlot.OFF_HAND) {
                            player.inventory.itemInOffHand.amount -= 1
                        }
                    }
                    KalaItems.SwordOfDisgust.v.kalaId -> {
                        val usage = 50
                        if (Main.expCoinsMap[player.uniqueId.toString()]!! < usage) {
                            player.sendMessage("${ChatColor.RED}충분한 잉여 경험치가 없어 역겨움과 혐오의 검을 작동시킬 수 없습니다.")
                        } else {
                            Main.expCoinsMap[player.uniqueId.toString()] = Main.expCoinsMap[player.uniqueId.toString()]!!.minus(usage)
                            player.sendMessage("${ChatColor.GOLD}50의 잉여 경험치를 사용하여 역겨움과 혐오의 검을 작동시켰습니다.")
                            player.playSound(player.location, "yangang.ho.wruful", SoundCategory.MASTER, 1f, 5f)
                            var fb = player.location.world!!.spawn(player.location.add(player.location.direction.multiply(2.75)), Fireball::class.java)

                            fb.direction = player.location.direction
                            fb.velocity = player.location.direction.multiply(1.25)
                            fb.isGlowing = true
                            fb.customName = "엮껴움"
                            fb.fireTicks = 100
                            fb.ticksLived = 100
                            fb.yield = 2f
                            fb.setMetadata("kalaId", FixedMetadataValue(Main().getPlugin(), "entity:disgustball"))
                        }
                    }
                }
            }
        }
    }

    fun useScrollOfTeleport(loc1: Location, loc2: Location, maxdist: Int): Boolean {
        var x1 = loc1.x
        var y1 = loc1.y
        var z1 = loc1.z
        var x2 = loc2.x
        var y2 = loc2.y
        var z2 = loc2.z


        return sqrt((x1-x2).pow(2) + (y1-y2).pow(2) + (z1-z2).pow(2) ) <= maxdist
    }
}