package yenru0.yrkaier.kalamenu

import org.bukkit.Bukkit
import net.minecraft.server.v1_15_R1.NBTTagCompound
import net.minecraft.server.v1_15_R1.ItemStack as nbtItemStack
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID
import org.bukkit.ChatColor
class KalaItem : KalaUtil {
    lateinit var itemstack : ItemStack
    var kalaId : String = ""
        set(v: String) {
            val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemstack)
            val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
            tag.setString("kalaId", v)
            nbt.tag = tag
            this.itemstack = CraftItemStack.asBukkitCopy(nbt)
            field = v
        }
    var kalaCost : Int = 0
        set(v: Int) {
            val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemstack)
            val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
            tag.setInt("kalaCost", v)
            nbt.tag = tag
            this.itemstack = CraftItemStack.asBukkitCopy(nbt)
            field = v
        }

    var kalaDurability : Int? = null
        set(v: Int?) {
            if (v != null){
                val nbt : nbtItemStack = CraftItemStack.asNMSCopy(itemstack)
                val tag : NBTTagCompound = nbt.tag ?: NBTTagCompound()
                tag.setInt("kalaDurability", v)
                nbt.tag = tag
                this.itemstack = CraftItemStack.asBukkitCopy(nbt)
                field = v
            }
        }

    constructor(itemstack: ItemStack, kalaId: String, kalaCost: Int) {
        this.itemstack = itemstack

        this.kalaId = kalaId // essential
        this.kalaCost = kalaCost
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

    constructor(type: Material, highlight: Boolean, kalaId: String, kalaCost: Int, name: String, lore: List<String>?) {
        itemstack = ItemStack(type)
        val itemmeta : ItemMeta = itemstack.itemMeta!!
        itemmeta.setDisplayName(name)
        itemmeta.lore = lore
        itemstack.itemMeta = itemmeta

        this.kalaId = kalaId // essential
        this.kalaCost = kalaCost
        if(highlight){
            itemstack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1)
        }
    }






}

enum class KalaItems (val v : KalaItem) {

    // Buy Item

    ScrollTeleportWeak(KalaItem(Material.PAPER, true, "item:scroll_teleport_weak", 499, "삭은 텔레포트의 주문서",
        listOf(
            "텔레포트의 주문서가 오랜 시간이 지나 삭았다.",
            "최대 1728 블럭을 텔레포트 할 수 있다. 우클릭해서 사용할 수 있다. (1회용)",
            "장소 지정은 '/settp <x> <y> <z>'로 지정할 수 있다.")
    )),
    ScrollTeleportNormal(KalaItem(Material.PAPER, true,"item:scroll_teleport_normal", 1899, "텔레포트의 주문서",
        listOf(
            "텔레포트 할 수 있다.",
            "최대 5832 블럭을 텔레포트 할 수 있다. 우클릭해서 사용할 수 있다. (1회용)",
            "장소 지정은 '/settp <x> <y> <z>'로 지정할 수 있다.")
    )),
    ScrollTeleportStrong(KalaItem(Material.PAPER, true,"item:scroll_teleport_strong", 3899, "강하고 힘쎈 텔레포트의 주문서",
        listOf(
            "먼 거리를 텔레포트 할 수 있다.",
            "최대 10648 블럭을 텔레포트 할 수 있다. 우클릭해서 사용할 수 있다. (1회용)",
            "장소 지정은 '/stetp <x> <y> <z>'로 지정할 수 있다.")
    )),

    // Unique Item
    HeadYangangNormal(KalaItem(let{
        val yhead = ItemStack(Material.PLAYER_HEAD)
        val yhead_im = yhead.itemMeta as SkullMeta
        yhead_im.owningPlayer = Bukkit.getOfflinePlayer(UUID.fromString("28f8e0c4-418d-42e2-b454-ca6e00244718"))
        yhead_im.setDisplayName("${ChatColor.GOLD}양갱의 일반적인 머리")
        yhead_im.lore = listOf(
            "양갱의 머리이다.",
            "조합 재료로 쓸 수 있을 것 같다.",
            "조합아이템"
        )
        yhead.itemMeta = yhead_im
        yhead
    }, "item:head_yangang_normal", 100)),

    // Reinforce Item
    BookEnchantSharpness6Yanganged(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("양갱의 날카로움 6 책")
        encbook_s_im.lore = listOf(
            "양갱 프리미엄:",
            "날카로움 VI",
            "(성공률: 75%)",
            "(소비: +1000)",
            "강화아이템"

        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_sharpness6_yanganged", 500)),

    BookEnchantKnockback3Yanganged(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("양갱의 날카로움 6 책")
        encbook_s_im.lore = listOf(
            "양갱 프리미엄:",
            "밀치기 III",
            "(성공률: 80%)",
            "(소비: +750)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_knockback3_yanganged", 375)
    ),

    BookEnchantSharpness5Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("날카로움 5 책")
        encbook_s_im.lore = listOf(
            "날카로움 V",
            "(성공률: 55%)",
            "(소비: +500)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_sharpness5_normal", 250)
    ),

    BookEnchantSharpness6Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("날카로움 6 책")
        encbook_s_im.lore = listOf(
            "날카로움 VI",
            "(성공률: 50%)",
            "(소비: +650)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_sharpness6_normal", 325)
    ),

    BookEnchantFortune3Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("행운 3 책")
        encbook_s_im.lore = listOf(
            "행운 III",
            "(성공률: 50%)",
            "(소비: +400)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_fortune3_normal", 200)
    ),

    BookEnchantFortune4Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 4)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("행운 4 책")
        encbook_s_im.lore = listOf(
            "행운 IV",
            "(성공률: 45%)",
            "(소비: +700)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_fortune4_normal", 375)
    ),
    BookEnchantFortune4Yanganged(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 4)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("양갱의 행운 4 책")
        encbook_s_im.lore = listOf(
            "양갱 프리미엄:",
            "행운 IV",
            "(성공률: 70%)",
            "(소비: +1200)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_fortune4_yanganged", 600)
    ),

    BookEnchantLoot3Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("약탈 3 책")
        encbook_s_im.lore = listOf(
            "약탈 III",
            "(성공률: 50%)",
            "(소비: +400)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_loot3_normal", 200)
    ),

    BookEnchantLoot4Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 4)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("약탈 4 책")
        encbook_s_im.lore = listOf(
            "약탈 IV",
            "(성공률: 45%)",
            "(소비: +700)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_loot4_normal", 350)
    ),

    BookEnchantFortune5Normal(KalaItem(let{
        val encbook_s = ItemStack(Material.ENCHANTED_BOOK)

        encbook_s.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5)
        val encbook_s_im = encbook_s.itemMeta

        encbook_s_im!!.setDisplayName("행운 5 책")
        encbook_s_im.lore = listOf(
            "행운 V",
            "(성공률: 40%)",
            "(소비: +1000)",
            "강화아이템"
        )
        encbook_s.itemMeta = encbook_s_im

        encbook_s
    }, "item:book_enchant_fortune5_normal", 500)
    ),

    SwordOfDisgust(KalaItem(let{
        val its = ItemStack(Material.WOODEN_SWORD)

        its.addUnsafeEnchantment(Enchantment.LUCK, 10)
        its.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 4)
        its.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 4)
        its.addUnsafeEnchantment(Enchantment.DURABILITY, 2)
        val itm: ItemMeta = its.itemMeta!!

        itm!!.setCustomModelData(10)
        itm.setDisplayName("역겨움과 혐오의 검")
        itm.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED,
            org.bukkit.attribute.AttributeModifier(
                UUID.randomUUID(), org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED.name,
                1.25, org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR,
                org.bukkit.inventory.EquipmentSlot.HAND
            )
        )

        itm.lore = listOf(
            "너무나 혐오스럽다,",
            "너무나 역겹다.",
            "우클릭을 하면 50의 잉여 경험치를 사용하여 ???를 한다.",
            "무기류"
        )
        its.itemMeta = itm

        its
    }, "item:sword_disgust", 500)
    ),

    DriverNormal(KalaItem(let{
        val its = ItemStack(Material.MUSIC_DISC_MALL)

        val itm: ItemMeta = its.itemMeta!!

        itm!!.setCustomModelData(10)
        itm.setDisplayName("드라이버")

        itm.lore = listOf(
            "간단한 드라이버다. 강화 확률을 절대치로 높여준다.",
            "(강화 성공 확률: +5%)",
            "(소비: +100)",
            "강화아이템"
        )
        its.itemMeta = itm

        its
    }, "item:driver_normal", 50)
    ),
    ShieldStone(KalaItem(let{
        val its = ItemStack(Material.SHIELD)
        its.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
        val itm: ItemMeta = its.itemMeta!!

        itm!!.setCustomModelData(10)
        itm.setDisplayName("양갱이 주문한 스톤")

        itm.lore = listOf(
            "양갱의 주문 제작",
            "매우 싸다."
        )
        its.itemMeta = itm

        its
    }, "item:shield_stone_normal", 200)
    )
}