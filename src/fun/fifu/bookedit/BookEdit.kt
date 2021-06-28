/**
Copyright (c) 2021 NekokeCore
EditBook is licensed under Mulan PSL v2.
You can use this software according to the terms and conditions of the Mulan PSL v2.
You may obtain a copy of Mulan PSL v2 at:
http://license.coscl.org.cn/MulanPSL2
THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
See the Mulan PSL v2 for more details.
 */

package `fun`.fifu.bookedit

import `fun`.fifu.bookedit.BookOperator.copyBook
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class BookEdit : JavaPlugin(), Listener {
    override fun onLoad() {
        fs = this
        File("plugins/BookEdit/").mkdirs()
    }

    override fun onEnable() {
        logger.info("欢迎使用BookEdit，author: NekokeCore")
        server.pluginManager.registerEvents(this, this)
        Bukkit.getPluginCommand("book")?.setExecutor(BookCommand())
    }

    @EventHandler
    fun onShiftBook(event: PlayerInteractEvent) {
//        println("qwq action=${event.action} isSneaking=${event.player.isSneaking} yaw=${event.player.location.yaw} pitch=${event.player.location.pitch}")
        if (!(event.action == Action.RIGHT_CLICK_AIR && event.player.isSneaking && event.player.location.pitch == -90f))
            return
        val bookMeta = (event.item ?: return).itemMeta ?: return
        if (bookMeta is BookMeta) {
            event.player.inventory.setItemInMainHand(bookMeta.copyBook())
        }
    }

    companion object {
        lateinit var fs: BookEdit

        @JvmStatic
        fun main(args: Array<String>) {
            println(
                "小白最可耐啊喵@光擦黑".split('@')[0]
            )
        }
    }
}