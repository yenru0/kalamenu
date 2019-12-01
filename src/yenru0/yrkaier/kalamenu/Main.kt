package yenru0.yrkaier.kalamenu

import net.minecraft.server.v1_14_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import yenru0.yrkaier.kalamenu.TypeInventory
import java.io.File
import kotlin.math.round
import kotlin.random.Random

class Main : JavaPlugin(), Listener {


    private lateinit var expCoinsMap : HashMap<String, Int>
    private lateinit var uidNameMap : HashMap<String, String>



    val ns = NamespacedKey(this, this.description.name)

    companion object {
        val plugin = this
        val log = Bukkit.getLogger()
        val pm = Bukkit.getPluginManager()

    }
    override fun onEnable() {

        pm.registerEvents(this, this)
        pm.registerEvents(KalaItemEvent(), this)
        log.info("${description.name} ${description.version} ${description.authors[0]} 활성화")
        val f = File(this.dataFolder.absolutePath, "kalamenu-playerdata.yml")
        if(f.exists()){
            val cf: YamlConfiguration = YamlConfiguration.loadConfiguration(f)
            this.expCoinsMap = cf.getConfigurationSection("players.expcoin")?.let { it.getValues(false) as HashMap<String, Int> } ?: hashMapOf()
            this.uidNameMap = cf.getConfigurationSection("players.name")?.let{ it.getValues(false) as HashMap<String, String>} ?: hashMapOf()
        } else {
            this.expCoinsMap = hashMapOf()
            this.uidNameMap = hashMapOf()

        }
    }

    override fun onDisable() {
        update_playerdata()
        log.info("${description.name}-${description.version} by ${description.authors[0]} 비활성화")
    }
    
    @EventHandler fun whenPlayerJoin(e: PlayerJoinEvent) {
        if (uidNameMap.containsKey(e.player.uniqueId.toString())){
            e.player.sendMessage("${ChatColor.AQUA}너는 ${e.player.name}이구나, 정말 대단해")
        }
        else {
            e.player.sendMessage("${ChatColor.AQUA}안녕 당신은 이곳이 처음인가요? 정말 대단하네요.")
            expCoinsMap[e.player.uniqueId.toString()] = 0
            uidNameMap[e.player.uniqueId.toString()] = e.player.name
        }

        update_playerdata()
    }

    @EventHandler fun whenPlayerCloseCustoma(e: InventoryCloseEvent){
        if (e.inventory.holder is TypeInventory){
            val typeinv = e.inventory.holder as TypeInventory
            when(typeinv.type){
                TypeInventory.TypeId.CCHEST -> {
                    val f : File = File(this.dataFolder.absolutePath, "${e.player.name}-chest.yml")
                    val cf: YamlConfiguration = YamlConfiguration()
                    val content : Array<ItemStack> = e.inventory.contents
                    cf.set("inventory.content", content)
                    cf.save(f)
                }
                TypeInventory.TypeId.SELL -> {

                }
            }
        }
    }



    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player : Player = sender as Player

        if (label.equals("chest", true)){
            val f : File = File(this.dataFolder.absolutePath, "${player.name}-chest.yml")
            val cf: YamlConfiguration = YamlConfiguration.loadConfiguration(f)
            val inv: TypeInventory
            if (f.exists()) {
                val content : Array<ItemStack> = (cf.get("inventory.content") as List<ItemStack>).toTypedArray()
                inv = TypeInventory(54, "${player.name}-chest", TypeInventory.TypeId.CCHEST)
                inv.inventory.contents = content
            }
            else {
                inv = TypeInventory(54, "${player.name}-chest", TypeInventory.TypeId.CCHEST)

            }
            player.openInventory(inv.inventory)
        }

        else if (label.equals("deposit", true)) {
            if (args.size == 1) {
                var amount: Int? = args[0].toIntOrNull()
                if (amount == null) {
                    if (args[0] == "all"){
                        player.exp = 0f
                        player.level = 0
                        amount = player.totalExperience
                        player.totalExperience = 0

                        add_expcoin_playerdata(player.uniqueId.toString(), amount)
                        player.sendMessage("${ChatColor.GOLD}경험치, ${amount}가 성공적으로 입금되었습니다.")
                    } else {
                        player.sendMessage("${ChatColor.RED}금액은 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                    }

                }
                else {
                    if (amount > 0) {
                        if (player.totalExperience - amount >= 0){
                            player.exp = 0f
                            player.level = 0
                            val xp = player.totalExperience - amount
                            player.totalExperience = 0
                            player.giveExp(xp)

                            add_expcoin_playerdata(player.uniqueId.toString(), amount)
                            player.sendMessage("${ChatColor.GOLD}경험치, ${amount}가 성공적으로 입금되었습니다.")
                        }
                        else {
                            player.sendMessage("${ChatColor.RED}입금할 경험치는 당신의 잔여 경험치 이하이어야 합니다.")
                        }
                    } else {
                        player.sendMessage("${ChatColor.RED}금액은 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED} 또는 ${ChatColor.DARK_RED}${ChatColor.BOLD}'all'${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                    }
                }
            } else {
                player.sendMessage("${ChatColor.RED}매개 변수는 오직 한개입니다.")
            }
        }

        else if (label.equals("pay", true)) {
            if(args.size != 2){
                player.sendMessage("${ChatColor.RED}사용방법: /pay {플레이어} {금액}")
            } else {
                val receiverName = args[0]
                val amount = args[1].toIntOrNull()
                if (amount == null || amount <= 0) {
                    player.sendMessage("${ChatColor.RED}매개 변수는 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                }
                else {
                    if (get_expcoin_playerdata(player.uniqueId.toString()) - amount < 0) {
                        player.sendMessage("${ChatColor.RED}전달할 경험치는 당신의 잉여 경험치보다 이하이어야 합니다.")
                    } else {
                        val receiverUnique : String? = find_uniqueid_playerdata(receiverName)
                        if (receiverUnique == null) {
                            player.sendMessage("${ChatColor.RED}일치하는 플레이어가 없습니다.")
                        } else {
                            add_expcoin_playerdata(player.uniqueId.toString(), -amount)
                            add_expcoin_playerdata(receiverUnique, amount)
                            player.sendMessage("${ChatColor.GOLD}잉여 경험치, ${amount}가 ${receiverName}에게 성공적으로 전달되었습니다.")
                        }
                    }
                }
            }
        }

        else if (label.equals("withdraw", true)) {
            if (args.size != 1){
                player.sendMessage("${ChatColor.RED}매개 변수는 오직 한개입니다.")
            } else {
                val amount : Int? = args[0].toIntOrNull()
                if (amount == null || amount <= 0) {
                    player.sendMessage("${ChatColor.RED}금액은 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                } else {
                    val remainAmount = get_expcoin_playerdata(player.uniqueId.toString())
                    if (remainAmount - amount < 0) {
                        player.sendMessage("${ChatColor.RED}출금할 경험치는 당신의 잉여 경험치 이하이어야 합니다.")
                    }
                    else {
                        add_expcoin_playerdata(player.uniqueId.toString(), -amount)

                        player.exp = 0f
                        player.level = 0
                        val coef = Random.nextDouble(0.94,1.0)
                        val xp = player.totalExperience + (amount * coef).toInt()
                        player.totalExperience = 0
                        player.giveExp(xp)

                        player.sendMessage("${ChatColor.GOLD}잉여 경험치, ${(amount * coef).toInt()}(${amount} × ${round(coef * 100)/100})가 성공적으로 출금되었습니다.")
                    }
                }
            }
        }

        else if (label.equals("credit", true)) {
            val amount = get_expcoin_playerdata(player.uniqueId.toString())
            player.sendMessage("${ChatColor.GOLD}당신의 잉여 경험치는 ${amount}입니다.")
        }

        else if (label.equals("buy", true)) {

        }

        else if (label.equals("tradebible", true)) {
            val remainAmount = get_expcoin_playerdata(player.uniqueId.toString())
            val amount = 16
            if (remainAmount - amount >= 0){
                player.inventory.addItem(KalaItems.Bible.v.itemstack)
                add_expcoin_playerdata(player.uniqueId.toString(), -amount)
                player.sendMessage("${ChatColor.GOLD}성경책(${amount} 경험치 어치)가 성공적으로 교환되었습니다.")
            } else {
                player.sendMessage("${ChatColor.RED}잉여 경험치가 부족합니다. 더 많은 경험치를 입금하세요.")
            }
        }

        return false
    }


    // player data relative functions

    fun find_uniqueid_playerdata(name:String) : String?{
        return uidNameMap.filterValues {it == name}.keys.firstOrNull()
    }

    // no null-safe
    fun add_expcoin_playerdata(k: String, a: Int){
        expCoinsMap.put(k, expCoinsMap.getValue(k) + a)
        update_playerdata()
    }

    fun get_expcoin_playerdata(k: String): Int {
        return expCoinsMap.getValue(k)
    }


    fun update_playerdata(){
        val f  = File(this.dataFolder.absolutePath, "kalamenu-playerdata.yml")
        val cf = YamlConfiguration()
        cf.set("players.expcoin", expCoinsMap)
        cf.set("players.name", uidNameMap)
        cf.save(f)
    }



}