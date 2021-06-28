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
import `fun`.fifu.bookedit.BookOperator.exportBook
import `fun`.fifu.bookedit.BookOperator.importBook
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BookCommand : TabExecutor {
    private val bookFiles = mutableListOf<String>()
    private val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时")

    private val helpMassage = mapOf(
        "help" to "/book help [command] 来查看帮助",
        "copy-to-writable-book" to "/book copy-to-writable-book 把主手的 成书/书与笔 复制成 书与笔 然后返还给玩家",
        "export-book" to "/book export-book-to-file <file> 把主手的 成书/书与笔 导出书到文件",
        "import-book" to "/book import-book <file> 从文件导入 书与笔",
        "view-book" to "/book view-book <file> [player] 给玩家打开一本成书，若 player 未填写，则为命令发送者"
    )

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        if (p0 !is Player) return mutableListOf()
        if (p3.size == 1) return helpMassage.keys.toMutableList()
        val ml = mutableListOf<String>()
        val playersName = mutableListOf<String>()
        Bukkit.getOnlinePlayers().forEach {
            playersName.add(it.name)
        }

        return when (p3[0]) {
            "import-book", "view-book" -> {
                bookFiles.clear()
                File("plugins/BookEdit/").listFiles()?.forEach {
                    if (it.isFile)
                        bookFiles.add(it.nameWithoutExtension)
                }
                bookFiles.distinct()
                bookFiles
            }
            "export-book" -> {
                bookFiles.clear()
                File("plugins/BookEdit/").listFiles()?.forEach {
                    if (it.isFile)
                        bookFiles.add(it.nameWithoutExtension.split('@')[0])
                }
                bookFiles.distinct()
                bookFiles
            }
            "help" -> {
                ml.addAll(helpMassage.keys)
                ml
            }
            else -> ml
        }
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
//        println(
//            """
//            p0:$p0
//            p1:$p1
//            p2:$p2
//            p3:${p3.contentToString()}
//        """.trimIndent()
//        )
        if (p0 !is Player) {
            p0.sendMessage("你必须是一名玩家")
            return true
        }
        if (p3.isNullOrEmpty()) return onHelp(p0, p3)
        try {
            val re = when (p3[0]) {
                "help" -> onHelp(p0, p3)
                "copy-to-writable-book" -> copyToWritableBook(p0)
                "export-book" -> exportBookToFile(p0, p3)
                "import-book" -> importBookFromFile(p0, p3)
                "view-book" -> viewBook(p0, p3)
                else -> false
            }
            if (!re) onHelp(p0, arrayOf("help", p3[0]))
        } catch (e: Exception) {
            onHelp(p0, arrayOf("help", p3[0]))
            BookEdit.fs.logger.warning("$p0 的命令 /book ${p3.contentToString()} 导致了一个异常：")
            e.printStackTrace()
            return true
        }
        return true

    }

    private fun viewBook(p0: Player, p3: Array<out String>): Boolean {
        if (p3.size == 1)
            return false
        if (!bookFiles.contains(p3[1]))
            return true

        if (p3.size <= 2) {
            p0.openBook(importBook("plugins/BookEdit/${p3[1].filt()}.txt", Material.WRITTEN_BOOK))
            p0.sendMessage("正在浏览书 ${p3[1].filt()}")
        } else {
            if (p0.isOp) {
                val player = Bukkit.getPlayer(p3[2])
                player?.openBook(importBook("plugins/BookEdit/${p3[1].filt()}.txt", Material.WRITTEN_BOOK))
                player?.sendMessage("正在浏览书 ${p3[1].filt()}")
            } else {
                p0.sendMessage("只有OP才有这个权限")
            }
        }
        return true
    }

    private fun importBookFromFile(p0: Player, p3: Array<out String>): Boolean {
        if (p3.size == 1)
            return false
        if (!bookFiles.contains(p3[1]))
            return true
        p0.inventory.addItem(importBook("plugins/BookEdit/${p3[1].filt()}.txt"))
        p0.sendMessage("成功导入书 ${p3[1].filt()}")
        return true
    }

    private fun exportBookToFile(p0: Player, p3: Array<out String>): Boolean {
        val bookMeta = p0.inventory.itemInMainHand.itemMeta ?: {
            p0.sendMessage("你主手必须持有 书/书与笔")
        }
        if (bookMeta is BookMeta) {
            bookMeta.exportBook("plugins/BookEdit/${p3[1].filt()}@${p0.name}_${myDateTimeFormatter.format(LocalDateTime.now())}.txt")
            p0.sendMessage("成功导出书 ${p3[1].filt()}")
        }
        return true
    }

    private fun copyToWritableBook(p0: Player): Boolean {
        val bookMeta = p0.inventory.itemInMainHand.itemMeta ?: {
            p0.sendMessage("你主手必须持有 书/书与笔")
        }
        if (bookMeta is BookMeta) {
            p0.inventory.addItem(bookMeta.copyBook())
            p0.sendMessage("成功复制书")
        }
        return true
    }


    private fun onHelp(player: Player, p3: Array<out String>): Boolean {
        if (p3.size == 1) {
            val sb = StringBuilder()
            helpMassage.values.forEach { sb.append(it).append("\n") }
            player.sendMessage("帮助：/book <command>\n$sb")
            return true
        } else {
            helpMassage[p3[1]]?.let { player.sendMessage(it) }
        }
        return true
    }

    fun String.filt() = this.replace(".", "").replace(":", "").replace("/", "").replace("\\", "")

}