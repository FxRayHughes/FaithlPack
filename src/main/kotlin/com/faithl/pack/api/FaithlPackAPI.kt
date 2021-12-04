package com.faithl.pack.api

import com.faithl.pack.common.inventory.Pack
import com.faithl.pack.common.util.deserializeToInventory
import com.faithl.pack.common.util.serializeToString
import com.faithl.pack.internal.data.Database
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import taboolib.common.platform.function.console
import taboolib.library.configuration.ConfigurationSection

object FaithlPackAPI {

    /**
     * 通过仓库名称获取仓库
     *
     * @param name 仓库名
     */
    fun getPack(name:String): Pack? {
        return Pack.packList.find {
            it.name == name
        }
    }

    /**
     * TabooLib ConfigurationSection创建仓库的解决方案
     *
     * @param root 仓库配置
     */
    fun createPack(root: ConfigurationSection){
        if (getPack(root.getString("Name")!!) == null){
            Pack(root)
        }else{
            console().sendMessage("&c[FaithlPack] The same name of pack already exists.")
        }
    }

    /**
     * 非TabooLib ConfigurationSection创建仓库的解决方案
     *
     * @param root 仓库配置
     */
    fun createPack(root: org.bukkit.configuration.ConfigurationSection){
        if (getPack(root.getString("Name")!!) == null){
            Pack(root as ConfigurationSection)
        }else{
            console().sendMessage("&c[FaithlPack] The same name of pack already exists.")
        }
    }

    /**
     * 从PackList中删除仓库
     *
     * @param pack 仓库
     */
    fun deletePack(pack:Pack){
        Pack.packList.remove(pack)
    }

    /**
     * 存储仓库
     *
     * @param player 玩家
     * @param pack 仓库
     * @param page 页数
     * @param value 仓库值
     */
    fun setPack(player: Player,pack: Pack,page: Int, value: String) {
        Database.INSTANCE.setPack(player,pack,page,value)
    }

    /**
     * 存储仓库
     *
     * @param player 玩家
     * @param pack 仓库
     * @param page 页数
     * @param value Bukkit容器
     */
    fun setPack(player: Player,pack: Pack,page: Int, value: Inventory) {
        Database.INSTANCE.setPack(player,pack,page,value.serializeToString())
    }

    /**
     * 获取仓库Inventory数据
     *
     * @param player 玩家
     * @param pack 仓库
     * @param page 页数
     * @return Inventory数据
     */
    fun getPackInventory(player: Player,pack: Pack,page: Int):Inventory?{
        return Database.INSTANCE.getPack(player,pack,page)?.deserializeToInventory()
    }

    /**
     * 获取仓库列表
     *
     * @return 仓库列表
     */
    fun getPackList():List<Pack>{
        return Pack.packList
    }
}