package yenru0.yrkaier.kalamenu

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.round
import kotlin.random.Random

class KalaItemEvent : KalaUtil(), Listener {
    @EventHandler
    fun whenUseKalaItem(e: PlayerInteractEvent){
        val player = e.player
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {

            if (e.item != null) {
                when (findKalaId(e.item!!)){

                    KalaItems.Bible.v.kalaId -> {
                        val healed = Random.nextInt(3, 5)
                        if (player.healthScale == player.health) {
                            player.sendMessage("${ChatColor.GOLD}이미 체력이 충분합니다!")
                        } else if (player.healthScale < player.health + healed) {
                            player.health = player.healthScale
                            if(EquipmentSlot.HAND == e.hand!!){player.inventory.itemInMainHand.amount -= 1 }
                            else {player.inventory.itemInOffHand.amount -= 1 }
                            player.sendMessage("${ChatColor.GOLD}기본 성경책으로 인해 당신은 피가 모두 회복되었습니다!")
                        } else {
                            player.health += healed
                            if(EquipmentSlot.HAND == e.hand!!){player.inventory.itemInMainHand.amount -= 1 }
                            else {player.inventory.itemInOffHand.amount -= 1 }
                            player.sendMessage("${ChatColor.GOLD}기본 성경책으로 인해 당신은 ${healed}가 회복되었습니다!")
                        }
                    }

                    KalaItems.BibleR.v.kalaId -> {
                        val healed = Random.nextInt(3, 5)
                        if (player.healthScale == player.health) {
                            player.sendMessage("${ChatColor.GOLD}이미 체력이 충분합니다!")
                        }
                        else if (player.healthScale < player.health + healed) {
                            player.health = player.healthScale
                            if(EquipmentSlot.HAND == e.hand!!){
                                if (player.inventory.itemInMainHand.amount > 1) {
                                    if(findKalaDurability(player.inventory.itemInMainHand)!! > 1) {
                                        player.inventory.itemInMainHand.amount -= 1
                                        val dumpItem = KalaUtil().breakKalaDurability(e.item!!.clone(), 1)
                                        dumpItem.amount = 1
                                        player.inventory.addItem(dumpItem)
                                    } else {
                                        player.inventory.itemInMainHand.amount -= 1
                                    }
                                } else {

                                    if(findKalaDurability(player.inventory.itemInMainHand)!! > 1) {
                                        val dumpItem = KalaUtil().breakKalaDurability(player.inventory.itemInMainHand, 1)
                                        player.inventory.setItemInMainHand(dumpItem)
                                    } else {
                                        player.inventory.itemInMainHand.amount -= 1
                                    }
                                }

                            }
                            else {
                                if (player.inventory.itemInOffHand.amount > 1) {
                                    if(findKalaDurability(player.inventory.itemInOffHand)!! > 1) {
                                        player.inventory.itemInOffHand.amount -= 1
                                        val dumpItem = KalaUtil().breakKalaDurability(e.item!!.clone(), 1)
                                        dumpItem.amount = 1
                                        player.inventory.addItem(dumpItem)
                                    } else {
                                        player.inventory.itemInOffHand.amount -= 1
                                    }
                                } else {

                                    if(findKalaDurability(player.inventory.itemInOffHand)!! > 1) {
                                        val dumpItem = KalaUtil().breakKalaDurability(player.inventory.itemInOffHand, 1)
                                        player.inventory.setItemInOffHand(dumpItem)
                                    } else {
                                        player.inventory.itemInOffHand.amount -= 1
                                    }
                                }
                            }
                            player.sendMessage("${ChatColor.GOLD}기본 성경책으로 인해 당신은 피가 모두 회복되었습니다!")
                        }
                        else {
                            player.health += healed
                            if(EquipmentSlot.HAND == e.hand!!){
                                if (player.inventory.itemInMainHand.amount > 1) {
                                    if(findKalaDurability(player.inventory.itemInMainHand)!! > 1) {
                                        player.inventory.itemInMainHand.amount -= 1
                                        val dumpItem = KalaUtil().breakKalaDurability(e.item!!.clone(), 1)
                                        dumpItem.amount = 1
                                        player.inventory.addItem(dumpItem)
                                    } else {
                                        player.inventory.itemInMainHand.amount -= 1
                                    }
                                } else {
                                    if(findKalaDurability(player.inventory.itemInMainHand)!! > 1) {
                                        val dumpItem = KalaUtil().breakKalaDurability(player.inventory.itemInMainHand, 1)
                                        player.inventory.setItemInMainHand(dumpItem)
                                    } else {
                                        player.inventory.itemInMainHand.amount -= 1
                                    }
                                }

                            }
                            else {
                                if (player.inventory.itemInOffHand.amount > 1) {
                                    if(findKalaDurability(player.inventory.itemInOffHand)!! > 1) {
                                        player.inventory.itemInOffHand.amount -= 1
                                        val dumpItem = KalaUtil().breakKalaDurability(e.item!!.clone(), 1)
                                        dumpItem.amount = 1
                                        player.inventory.addItem(dumpItem)
                                    } else {
                                        player.inventory.itemInOffHand.amount -= 1
                                    }
                                } else {
                                    if(findKalaDurability(player.inventory.itemInOffHand)!! > 1) {
                                        val dumpItem = KalaUtil().breakKalaDurability(player.inventory.itemInOffHand, 1)
                                        player.inventory.setItemInOffHand(dumpItem)
                                    } else {
                                        player.inventory.itemInOffHand.amount -= 1
                                    }
                                }
                            }
                            player.sendMessage("${ChatColor.GOLD}기본 성경책으로 인해 당신은 ${healed}가 회복되었습니다!")
                        }
                    }
                }
            }
        }
    }
}