package yenru0.yrkaier.kalamenu

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.round
import kotlin.random.Random

class KalaItemEvent : KalaUtil(), Listener {
    @EventHandler
    fun whenUseKalaItem(e: PlayerInteractEvent){
        val player = e.player
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            if (findKalaId(player.inventory.itemInMainHand) == KalaItems.Bible.v.kalaId){
                player.inventory.itemInMainHand.amount -= 1
                val healed = (Random.nextInt(2, 5).toDouble()) / 2
                e.player.health += healed
                player.sendMessage("${ChatColor.GOLD}기본 성경책으로 인해 당신은 ${ round(healed*100/100) }가 회복되었습니다!")
            }
        }
    }
}