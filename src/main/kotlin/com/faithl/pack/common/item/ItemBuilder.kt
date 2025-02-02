package com.faithl.pack.common.item

import com.faithl.pack.api.FaithlPackAPI
import com.faithl.pack.common.core.PackData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.parseToXMaterial
import taboolib.module.chat.colored
import taboolib.module.nms.getItemTag
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.buildItem

object ItemBuilder {

    fun buildInventory(player: Player, packData: PackData, page: Int, rows: Int): Inventory {
        val inventory = Bukkit.createInventory(null, rows * 9)
        packData.getPageItems(page).forEach {
            inventory.setItem(it.key, it.value)
        }
        if (packData.getSetting().lock) {
            val unlockedSize = FaithlPackAPI.getUnlockedSize(player, packData)
            val lockedSize = (rows - 1) * 9 * page - unlockedSize
            if (lockedSize > 0) {
                val lockedItemStack = getNBTItemStack(player, packData, page, "lockedItemStack")
                for (slot in (rows - 1) * 9 - lockedSize until (rows - 1) * 9) {
                    inventory.setItem(slot, lockedItemStack)
                }
            }
        }
        val pageItemStack = getNBTItemStack(player, packData, page, "page")
        inventory.setItem(rows * 9 - 5, pageItemStack)
        val nullItemStack = getNBTItemStack(player, packData, page, "null")
        listOf(
            rows * 9 - 2,
            rows * 9 - 3,
            rows * 9 - 4,
            rows * 9 - 6,
            rows * 9 - 7,
            rows * 9 - 8,
            rows * 9 - 9
        ).forEach {
            inventory.setItem(it, nullItemStack)
        }
        val autoPickupItemStack = getNBTItemStack(player, packData, page, "setting.auto-pickup")
        inventory.setItem(rows * 9 - 1, autoPickupItemStack)
        return inventory
    }

    fun getNBTItemStack(player: Player, packData: PackData, page: Int, type: String): ItemStack {
        return getItemStack(player, packData, page, type).apply {
            getItemTag().also { itemTag ->
                itemTag.putDeep("pack.type", type)
                itemTag.saveTo(this)
            }
        }
    }

    fun getItemStack(player: Player, packData: PackData, page: Int, item: String): ItemStack {
        val packSetting = packData.getSetting()
        return buildItem(
            packSetting.inventory?.getString("items.${item}.display.material")?.parseToXMaterial()
                ?: XMaterial.GRAY_STAINED_GLASS
        ) {
            name = packSetting.inventory?.getString("items.${item}.display.name")?.colored()?.replacePlaceholder(player)
                ?.replace("{page}", page.toString())
                ?.replace("{pages}", packSetting.inventory.getInt("pages").toString()) ?: ""
            packSetting.inventory?.getStringList("items.${item}.display.lore")?.colored()?.forEach {
                lore += it.replacePlaceholder(player).replace("{page}", page.toString()).replace(
                    "{pages}",
                    packSetting.inventory.getInt("pages").toString()
                )
            }
            for (s in packSetting.inventory?.getStringList("items.${item}.display.enchants") ?: mutableListOf()) {
                if (s.isEmpty()) {
                    break
                }
                val enchant = s.split(":")[0]
                val level = Coerce.toInteger(s.split(":")[1])
                enchants[XEnchantment.valueOf(enchant).parseEnchantment()!!] =
                    (enchants[XEnchantment.valueOf(enchant).parseEnchantment()!!] ?: 0) + level
            }
            if (packSetting.inventory?.getBoolean("items.${item}.display.shiny") == true) {
                shiny()
            }
        }
    }

}