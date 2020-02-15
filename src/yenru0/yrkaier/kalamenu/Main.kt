package yenru0.yrkaier.kalamenu

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Furnace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import yenru0.yrkaier.kalamenu.TypeInventory
import java.io.File
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random
import com.google.common.math.IntMath.pow as intpow
import yenru0.yrkaier.kalamenu.KalaUtil
import java.util.*
import kotlin.collections.HashMap

//TODO: In 0.66H
//TODO: 강화 아이템 추가 (다양화) <L>

class Main : JavaPlugin(), Listener {

    val ku = KalaUtil()

    companion object {
        // player data
        lateinit var expCoinsMap: HashMap<String, Int>
        lateinit var uidNameMap: HashMap<String, String>
        lateinit var remainLifeMap: HashMap<String, Int>
        lateinit var settpMap: HashMap<String, Location>
        lateinit var lastDeathMap: HashMap<String, Location>
        lateinit var chestMap: HashMap<String, Array<ItemStack?>>

        //loc based data
        lateinit var furnaceLocaitionMap: HashMap<String, Double>

        var frcs : MutableList<FurnaceRecipe> = mutableListOf()
        val plugin = this
        val log = Bukkit.getLogger()
        val pm = Bukkit.getPluginManager()

    }


    override fun onEnable() {
        val frs : MutableList<FurnaceRecipe> = mutableListOf()

        Bukkit.recipeIterator().forEach{
            if (it is FurnaceRecipe){
                frs.add(it)
                print("ok")
            }
        }
        frs.forEach{
            var keyname = it.key.toString()
            keyname = keyname.split(":").last()

            val frc = FurnaceRecipe(NamespacedKey(this, keyname), it.result, it.inputChoice, it.experience * 2.75f, it.cookingTime)
            frcs.add(frc)

            Bukkit.getServer().removeRecipe(it.key)
            Bukkit.getServer().addRecipe(frc)

        }


        // config
        pm.registerEvents(this, this)
        pm.registerEvents(KalaItemEvent(), this)
        log.info("${description.name} ${description.version} ${description.authors[0]} 활성화")

        val ff = File(this.dataFolder.absolutePath, "kalamenu-location-baseddata.yml")

        if (ff.exists()){
            val cff: YamlConfiguration = YamlConfiguration.loadConfiguration(ff)
            furnaceLocaitionMap = cff.getConfigurationSection("players.furnaces")?.let { it.getValues(false) as HashMap<String, Double> } ?: hashMapOf()
        } else {
            furnaceLocaitionMap = hashMapOf()
        }

        lastDeathMap = hashMapOf()
        val f = File(this.dataFolder.absolutePath, "kalamenu-playerdata.yml")


        if (f.exists()) {
            val cf: YamlConfiguration = YamlConfiguration.loadConfiguration(f)
            expCoinsMap = cf.getConfigurationSection("players.expcoin")?.let { it.getValues(false) as HashMap<String, Int> } ?: hashMapOf()
            uidNameMap = cf.getConfigurationSection("players.name")?.let { it.getValues(false) as HashMap<String, String> } ?: hashMapOf()
            remainLifeMap = cf.getConfigurationSection("players.life")?.let { it.getValues(false) as HashMap<String, Int> } ?: hashMapOf()
            settpMap = cf.getConfigurationSection("players.settp")?.let { it.getValues(false) as HashMap<String, Location> } ?: hashMapOf()
        } else {
            expCoinsMap = hashMapOf()
            uidNameMap = hashMapOf()
            remainLifeMap = hashMapOf()
            settpMap = hashMapOf()
        }

        // scoreboards
        val board = Bukkit.getScoreboardManager()?.newScoreboard!!
        val obj_exc = board.registerNewObjective("PECV", "dummy", "경험치 코인 값")
        obj_exc.displaySlot = DisplaySlot.SIDEBAR

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            this,
            Runnable {
                for (i in server.onlinePlayers) {
                    var score = obj_exc.getScore(i.name)
                    score.score = expCoinsMap[i.uniqueId.toString()]!!
                    i.scoreboard = board
                }


            },
            0L, 25L
        )

        val obj_life = board.registerNewObjective("DeathLife", "dummy", "죽음 계수")
        obj_life.displaySlot = DisplaySlot.PLAYER_LIST

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            this,
            Runnable {
                for (i in server.onlinePlayers) {
                    var score = obj_life.getScore(i.name)
                    score.score = remainLifeMap[i.uniqueId.toString()]!!
                    i.scoreboard = board
                }
            },
            0L, 200L
        )


        // Dogengwaje per 10min
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            this,
            Runnable {
                val advancementWithoutRecipes = server.advancementIterator().asSequence().asIterable().filter { !it.key.key.startsWith("recipes") }


                val advsize = advancementWithoutRecipes.count()

                server.broadcastMessage("${ChatColor.RED}====={도전과제 올클리어}=====")
                print(server.onlinePlayers)
                for (i in server.onlinePlayers) {
                    var count = 0

                    advancementWithoutRecipes.forEach {
                        if (i.getAdvancementProgress(it).isDone)
                            count += 1
                    }

                    server.broadcastMessage("${ChatColor.RED}${i.name}: (${count}/${advsize}) ${(round(count.div(advsize.toDouble()) * 10000) / 100.0)}%")
                }


            },
            0L, (360 * 20).toLong()
        )

    }

    override fun onDisable() {
        update_playerdata()
        log.info("${description.name}-${description.version} by ${description.authors[0]} 비활성화")
    }

    @EventHandler
    fun whenPlayerJoin(e: PlayerJoinEvent) {
        if (uidNameMap.containsKey(e.player.uniqueId.toString())) {
            e.player.sendMessage("${ChatColor.AQUA}gd")
            if (settpMap[e.player.uniqueId.toString()] == null) {
                settpMap[e.player.uniqueId.toString()] = Bukkit.getWorld("world")!!.spawnLocation
            }
        } else {
            e.player.sendMessage("${ChatColor.AQUA}ㅎㅇ, ${e.player.name}")
            expCoinsMap[e.player.uniqueId.toString()] = 0
            uidNameMap[e.player.uniqueId.toString()] = e.player.name
            remainLifeMap[e.player.uniqueId.toString()] = 0

            settpMap[e.player.uniqueId.toString()] = Bukkit.getWorld("world")!!.spawnLocation
        }

        update_playerdata()
    }



    @EventHandler
    fun whenPlayerCloseCustoma(e: InventoryCloseEvent) {
        if (e.inventory.holder is TypeInventory) {
            val typeinv = e.inventory.holder as TypeInventory
            when (typeinv.type) {
                TypeInventory.TypeId.CCHEST -> {
                    val f: File = File(this.dataFolder.absolutePath, "${e.player.name}-chest.yml")
                    val cf: YamlConfiguration = YamlConfiguration()
                    val content: Array<ItemStack> = e.inventory.contents
                    cf.set("inventory.content", content)
                    cf.save(f)
                }

                else -> return
            }
        }
    }



    @EventHandler
    fun whenPlayerClickInventory(e: InventoryClickEvent) {
        val player = e.whoClicked as Player
        if (e.inventory.holder is TypeInventory) {
            val typeinv = e.inventory.holder as TypeInventory
            if(e.click == ClickType.NUMBER_KEY){
                return
            }
            when (typeinv.type) {
                TypeInventory.TypeId.BUY -> {

                    if (e.currentItem != null) {
                        if (e.clickedInventory!!.holder !is TypeInventory &&
                            e.click != ClickType.DROP
                        ) {
                            return
                        }


                        if (ku.findKalaId(e.currentItem!!) != "") {
                            val remainAmount = get_expcoin_playerdata(player.uniqueId.toString())
                            val amount = ku.findKalaCost(e.currentItem!!) ?: let { e.isCancelled = true; return }
                            if (remainAmount - amount >= 0) {
                                player.inventory.addItem(e.currentItem)
                                var t: KalaItem

                                add_expcoin_playerdata(player.uniqueId.toString(), -amount)
                                player.sendMessage("${ChatColor.GOLD}${e.currentItem!!.itemMeta!!.displayName}(${amount} 경험치 어치)가 성공적으로 교환되었습니다.")
                            } else {
                                player.sendMessage("${ChatColor.RED}잉여 경험치가 부족합니다. 더 많은 경험치를 입금하세요.")
                            }
                        }

                        e.isCancelled = true
                    }
                }

                TypeInventory.TypeId.REINFORCE -> {
                    if (e.clickedInventory!!.holder is TypeInventory){
                        if (e.slot in listOf(10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39,     14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43 )){
                            return
                        } else if (e.slot in listOf(4, 13, 22, 31, 40, 49)){


                            val left = e.inventory.contents.filterIndexed{i, v -> i in listOf(10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39)}
                            val right = e.inventory.contents.filterIndexed{i, v -> i in listOf(14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43)}

                            var temp = ku.reinforcement(left, right, player)
                            if (temp != null) {
                                for (i in temp){
                                    player.inventory.addItem(i)
                                }

                            }
                            e.isCancelled = true
                            player.openInventory(TypeInventory(type= TypeInventory.TypeId.REINFORCE).inventory)





                        } else {
                            e.isCancelled = true
                        }
                    }
                }

                else -> {
                    return
                }

            }
        }
    }

    @EventHandler
    fun whenPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity
        val playerDeathLoc = player.location
        lastDeathMap[player.uniqueId.toString()] = player.location
        Bukkit.broadcastMessage(
            "${ChatColor.DARK_PURPLE}${player.name}의 시체가 ${playerDeathLoc.world!!.name}의 (${playerDeathLoc.blockX}, ${playerDeathLoc.blockY}, ${playerDeathLoc.blockZ})에 있습니다."
        )
    }

    @EventHandler
    fun whenPlayerRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        val playerUid = player.uniqueId.toString()
        player.teleport(lastDeathMap[player.uniqueId.toString()] ?: Bukkit.getWorld("world")!!.spawnLocation)
        player.sendTitle("${ChatColor.DARK_RED}YOU DIED", "${ChatColor.RED}${20}초 후 부활합니다.", 20, 10 * 20, 80)
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            this, Runnable {
                if (player.gameMode != GameMode.CREATIVE){
                    player.gameMode = GameMode.SURVIVAL
                }
                player.sendTitle("${ChatColor.WHITE}RE-BIRTH", null, 1, 3*20, 4)
                player.playSound(player.location, "yangang.ho.wruful", SoundCategory.MASTER, 5f, 5f)
            },
            20 * 20
        )

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            this, Runnable {
                player.sendTitle("${ChatColor.RED}10초 후 부활합니다.", null, 20, 5 * 20, 20)
            },
            20 * 20 - 11 * 20
        )

        remainLifeMap[playerUid] = remainLifeMap[playerUid]!!.plus(1)


        update_playerdata()
    }

    @EventHandler
    fun makeMonsterGreatAgain(e: CreatureSpawnEvent) {
        val mob = e.entity
        when (e.entityType) {
            EntityType.ZOMBIE -> {
                val rnds = (1..10000).random()
                if( rnds <= 750){
                    mob.getAttribute(Attribute.GENERIC_ARMOR)!!.baseValue += 15
                    mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += 20
                    mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:zombie_yanganged"))

                    mob.equipment!!.helmet = KalaItems.HeadYangangNormal.v.itemstack
                    mob.equipment!!.helmetDropChance = 0f
                } else {
                    mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += 5
                    mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:zombie_normal"))
                }

            }

            EntityType.ZOMBIE_VILLAGER -> {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += 5
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:zombieVillager_normal"))
            }

            EntityType.SKELETON -> {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += 1
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:skeleton_normal"))
            }

            EntityType.PIG_ZOMBIE -> {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue -= 1

                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:pigZombie_normal"))
            }

            EntityType.CREEPER -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:creeper_normal"))

            }

            EntityType.BLAZE -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:blaze_normal"))
            }

            EntityType.WITHER_SKELETON -> {
                val rnds = (1..1000).random()
                if (rnds in 1..85){
                    mob.equipment!!.setItemInMainHand(ItemStack(Material.BOW))
                    mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:witherSkeleton_archer"))
                    mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:witherSkeleton_normal"))
                }else {
                    mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:witherSkeleton_normal"))
                }

            }



            EntityType.BAT -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:bat_normal"))
            }

            EntityType.PILLAGER -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:pillager_normal"))
            }

            EntityType.SPIDER -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:spider_normal"))
            }

            EntityType.CAVE_SPIDER -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:spider_cave"))
            }

            EntityType.WITCH -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:witch_normal"))
            }

            EntityType.PHANTOM -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:phantom_normal"))
            }

            EntityType.HUSK -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:husk_normal"))
            }
            EntityType.MAGMA_CUBE -> {
                mob.setMetadata("kalaId", FixedMetadataValue(this, "monster:magmaCube_normal"))
            }


        }
        mob.health = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
    }



    @EventHandler
    fun whenMobDeath(e: EntityDeathEvent) {

        when(e.entity.getMetadata("kalaId").firstOrNull()?.asString()) {
            "monster:zombie_normal", "monster:zombieVillager_normal", "monster:skeleton_normal",
                "monster:creeper_normal", "monster:pillager_normal", "monster:spider_normal",
            "monster:spider_cave", "monster:witch_normal", "monster:husk_normal", "monster:magmaCube_normal",/*바뀔수도*/
            "monster:phantom_normal"-> {
                e.droppedExp = Random.nextInt(7, 11)
                val rnds = (1..1000).random()
                if (rnds in 1..4) {
                    e.drops.add(KalaItems.BookEnchantSharpness5Normal.v.itemstack)
                } else if(rnds in 5..6) {
                    e.drops.add(KalaItems.BookEnchantSharpness6Normal.v.itemstack)
                } else if (rnds in 7..10) {
                    e.drops.add(KalaItems.BookEnchantFortune3Normal.v.itemstack)
                }
            }
            "monster:pigZombie_normal" -> {
                e.droppedExp = Random.nextInt(8, 13)
                val rnds = (1..1000).random()
                if (rnds in 1..3){
                    e.drops.add(KalaItems.BookEnchantFortune4Normal.v.itemstack)
                } else if( rnds in 4..5){
                    e.drops.add(KalaItems.BookEnchantSharpness6Normal.v.itemstack)
                }
                else if (rnds in 6..11) {
                    e.drops.add(KalaItems.BookEnchantFortune3Normal.v.itemstack)
                } else if (rnds in 12..14) {
                    e.drops.add(KalaItems.BookEnchantSharpness5Normal.v.itemstack)
                } else if (rnds in 15..24) {
                    e.drops.add(KalaItems.ScrollTeleportNormal.v.itemstack)
                }
            }

            "monster:blaze_normal" -> {
                e.droppedExp = Random.nextInt(10, 20)
                val rnds = (1..1000).random()
                if (rnds in 1..6){
                    e.drops.add(KalaItems.BookEnchantFortune4Normal.v.itemstack)
                }
                else if (rnds in 9..18) {
                    e.drops.add(KalaItems.BookEnchantKnockback3Yanganged.v.itemstack)
                } else if (rnds in 19..21) {
                    e.drops.add(KalaItems.ScrollTeleportStrong.v.itemstack)
                }
            }

            "monster:witherSkeleton_normal" -> {
                e.droppedExp = Random.nextInt(8, 18)
                val rnds = (1..1000).random()
                if (rnds in 1..2){
                    e.drops.add(KalaItems.BookEnchantLoot4Normal.v.itemstack)
                }
                else if (rnds in 3..8){
                    e.drops.add(KalaItems.BookEnchantLoot3Normal.v.itemstack)
                }
                else if (rnds in 9..18) {
                    e.drops.add(KalaItems.BookEnchantKnockback3Yanganged.v.itemstack)
                } else if (rnds in 19..21) {
                    e.drops.add(KalaItems.ScrollTeleportStrong.v.itemstack)
                }
            }
            // 요카지마 활 추가 예정
            "monster:witherSkeleton_archer" -> {
                e.droppedExp = Random.nextInt(9, 19)
                val rnds = (1..1000).random()
                if (rnds in 1..2){
                    e.drops.add(KalaItems.BookEnchantLoot4Normal.v.itemstack)
                }
                else if (rnds in 3..8){
                    e.drops.add(KalaItems.BookEnchantLoot3Normal.v.itemstack)
                }
                else if (rnds in 9..18) {
                    e.drops.add(KalaItems.BookEnchantKnockback3Yanganged.v.itemstack)
                } else if (rnds in 19..21) {
                    e.drops.add(KalaItems.ScrollTeleportStrong.v.itemstack)
                }
            }

            "monster:zombie_yanganged" -> {
                val rnds = (1..1000).random()
                e.droppedExp = Random.nextInt(32,50)
                if(rnds <= 150) {
                    e.drops.add(KalaItems.HeadYangangNormal.v.itemstack)
                }
                else if(rnds in 151..155){
                    e.drops.add(KalaItems.BookEnchantSharpness6Yanganged.v.itemstack)

                }

                else if (rnds in 156..160) {
                    e.drops.add(KalaItems.BookEnchantKnockback3Yanganged.v.itemstack)
                } else if (rnds in 161..180) {
                    e.drops.add(KalaItems.SwordOfDisgust.v.itemstack)
                }

            }
            else -> {}
        }
        log.info(e.entity.getMetadata("kalaId").firstOrNull()?.asString() + "${e.entity.killer?.name}")

    }

    @EventHandler
    fun whenMobDamaged(e: EntityDamageByEntityEvent) {
        when (e.damager.getMetadata("kalaId").firstOrNull()?.asString()){
            "entity:disgustball" -> {
                e.damage += 4
            }
        }
    }


    
    @EventHandler
    fun whenFurnaceSmelt(e: FurnaceSmeltEvent) {
        val temp = furnaceLocaitionMap.get(getLocationString(e.block.location)) ?:  0.0
        val temp_add = getRecipeFurnace(e.source, e.result).toDouble()
        furnaceLocaitionMap[getLocationString(e.block.location)] = temp.plus(temp_add)

        update_playerdata()
    }

    @EventHandler
    fun whenFurnaceExtract(e: FurnaceExtractEvent){
        val temp: Double? = furnaceLocaitionMap[getLocationString(e.block.location)]

        e.expToDrop = temp?.toInt() ?: 1

        furnaceLocaitionMap[getLocationString(e.block.location)] = 0.0

        update_playerdata()
    }



    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val player: Player = sender as Player

        if (label.equals("deposit", true) || label.equals("입금", true)) {
            if (args.size == 1) {
                var amount: Int? = args[0].toIntOrNull()
                if (amount == null) {
                    if (args[0] == "all") {

                        amount = getTotalExperience(player.level, player.exp)
                        player.exp = 0f
                        player.level = 0
                        player.totalExperience = 0

                        add_expcoin_playerdata(player.uniqueId.toString(), amount)
                        player.sendMessage("${ChatColor.GOLD}경험치, ${amount}가 성공적으로 입금되었습니다.")
                    } else {
                        player.sendMessage("${ChatColor.RED}금액은 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                    }

                } else {
                    if (amount > 0) {
                        if (getTotalExperience(player.level, player.exp) - amount >= 0) {

                            val xp = getTotalExperience(player.level, player.exp) - amount
                            player.exp = 0f
                            player.level = 0
                            player.totalExperience = 0
                            player.giveExp(xp)

                            add_expcoin_playerdata(player.uniqueId.toString(), amount)
                            player.sendMessage("${ChatColor.GOLD}경험치, ${amount}가 성공적으로 입금되었습니다.")
                        } else {
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

        else if (label.equals("pay", true) || label.equals("송금", true)) {
            if (args.size != 2) {
                player.sendMessage("${ChatColor.RED}사용방법: /pay {플레이어} {금액}")
            } else {
                val receiverName = args[0]
                val amount = args[1].toIntOrNull()
                if (amount == null || amount <= 0) {
                    player.sendMessage("${ChatColor.RED}매개 변수는 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                } else {
                    if (get_expcoin_playerdata(player.uniqueId.toString()) - amount < 0) {
                        player.sendMessage("${ChatColor.RED}전달할 경험치는 당신의 잉여 경험치보다 이하이어야 합니다.")
                    } else {
                        val receiverUnique: String? = find_uniqueid_playerdata(receiverName)
                        if (receiverUnique == null) {
                            player.sendMessage("${ChatColor.RED}일치하는 플레이어가 없습니다.")
                        } else {
                            add_expcoin_playerdata(player.uniqueId.toString(), -amount)
                            add_expcoin_playerdata(receiverUnique, amount)
                            player.sendMessage("${ChatColor.GOLD}잉여 경험치, ${amount}가 ${receiverName}에게 성공적으로 전달되었습니다.")
                            Bukkit.getPlayer(receiverName)!!.sendMessage("${ChatColor.AQUA}${player.name}으로부터 잉여 경험치, ${amount}가 왔습니다.")
                        }
                    }
                }
            }
        }

        else if(label.equals("withdraw", true) || label.equals("출금", true)) {
            if (args.size != 1) {
                player.sendMessage("${ChatColor.RED}매개 변수는 오직 한개입니다.")
            } else {
                val amount: Int? = args[0].toIntOrNull()
                if (amount == null || amount <= 0) {
                    player.sendMessage("${ChatColor.RED}금액은 ${ChatColor.DARK_RED}${ChatColor.BOLD}자연수${ChatColor.RESET}${ChatColor.RED}이어야 합니다.")
                } else {
                    val remainAmount = get_expcoin_playerdata(player.uniqueId.toString())
                    if (remainAmount - amount < 0) {
                        player.sendMessage("${ChatColor.RED}출금할 경험치는 당신의 잉여 경험치 이하이어야 합니다.")
                    } else {
                        add_expcoin_playerdata(player.uniqueId.toString(), -amount)

                        val coef = 1.0
                        val xp = getTotalExperience(player.level, player.exp) + (amount * coef).toInt()
                        player.exp = 0f
                        player.level = 0
                        player.totalExperience = 0
                        player.giveExp(xp)

                        player.sendMessage("${ChatColor.GOLD}잉여 경험치, ${(amount * coef).toInt()}(${amount} × ${round(coef * 100) / 100})가 성공적으로 출금되었습니다.")
                    }
                }
            }
        }

        else if (label.equals("credit", true) || label.equals("돈", true)) {
            val amount = get_expcoin_playerdata(player.uniqueId.toString())
            player.sendMessage("${ChatColor.GOLD}당신의 잉여 경험치는 ${amount}입니다.")
        }

        else if (label.equals("buy", true)) {
            player.openInventory(TypeInventory("", type = TypeInventory.TypeId.BUY).inventory)
        }

        else if (label.equals("life", true)) {
            player.sendMessage("${ChatColor.RED}${remainLifeMap[player.uniqueId.toString()]}")
        }

        else if (label.equals("distance", true) || label.equals("거리", true)) {
            if (player.world.name != settpMap[player.uniqueId.toString()]!!.world!!.name) {
                player.sendMessage("월드는 같아야 합니다.")
            } else {
                var px = player.location.x
                var py = player.location.y
                var pz = player.location.z
                var sx = settpMap[player.uniqueId.toString()]!!.x
                var sy = settpMap[player.uniqueId.toString()]!!.y
                var sz = settpMap[player.uniqueId.toString()]!!.z

                player.sendMessage("${ChatColor.AQUA}${sqrt((px - sx).pow(2) + (py - sy).pow(2) + (pz - sz).pow(2))}")

            }
        }

        else if (label.equals("settp", true)) {
            if (args.size != 3) {
                player.sendMessage("${ChatColor.RED}매개 변수는 무조건 3개입니다.")
            } else {
                var x = args[0].toDoubleOrNull()
                var y = args[1].toDoubleOrNull()
                var z = args[2].toDoubleOrNull()

                if (x == null || y == null || z == null) {
                    player.sendMessage("${ChatColor.RED}매개 변수는 무조건 수여야합니다.")
                } else {
                    settpMap[player.uniqueId.toString()] = Location(player.world, x, y, z)
                }
            }
        }

        else if (label.equals("reinforce", true) || label.equals("강화", true)) {
            player.openInventory(TypeInventory(type=TypeInventory.TypeId.REINFORCE).inventory)
        }
        else if (label.equals("debug", true)){
            player.inventory.addItem(KalaItems.SwordOfDisgust.v.itemstack)
        }
        return false

    }


    // player data relative functions

    fun find_uniqueid_playerdata(name: String): String? {
        return uidNameMap.filterValues { it == name }.keys.firstOrNull()
    }

    // no null-safe
    fun add_expcoin_playerdata(k: String, a: Int) {
        expCoinsMap.put(k, expCoinsMap.getValue(k) + a)
        update_playerdata()
    }

    fun get_expcoin_playerdata(k: String): Int {
        return expCoinsMap.getValue(k)
    }

    fun update_playerdata() {
        val f = File(this.dataFolder.absolutePath, "kalamenu-playerdata.yml")
        val cf = YamlConfiguration()
        cf.set("players.expcoin", expCoinsMap)
        cf.set("players.name", uidNameMap)
        cf.set("players.life", remainLifeMap)
        cf.set("players.settp", settpMap)
        cf.save(f)

        val ff = File(this.dataFolder.absolutePath, "kalamenu-location-baseddata.yml")
        val cff = YamlConfiguration()
        cff.set("players.furnaces", furnaceLocaitionMap)
        cff.save(ff)
    }

    fun getExpAtLevel(level: Int): Int {
        return if (level <= 16) {
            (intpow(level, 2) + 6 * level).toInt()
        } else if (level <= 31) {
            (2.5 * intpow(level, 2) - 40.5 * level + 360.0).toInt()
        } else {
            (4.5 * intpow(level, 2) - 162.5 * level + 2220.0).toInt()
        }
    }

    fun getExpToLevelUp(level: Int): Int {
        return if (level <= 15) {
            2 * level + 7
        } else if (level <= 30) {
            5 * level - 38
        } else {
            9 * level - 158
        }
    }

    fun getTotalExperience(level: Int, exp: Float): Int {
        return getExpAtLevel(level) + round(getExpToLevelUp(level) * exp).toInt()
    }

    fun getRecipeFurnace(source: ItemStack, output: ItemStack): Float {
        return frcs.find {
            source.type == it.input.type && output.type == it.result.type
        }!!.experience
    }

    fun getLocationString(loc: Location) : String {
        return "${loc.world!!.name};${loc.blockX};${loc.blockY};${loc.blockZ}"
    }

    fun getPlugin(): Plugin {
        return this
    }
}