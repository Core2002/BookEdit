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

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import java.io.File

object BookOperator {
    /**
     * 复制一本书与笔
     */
    fun BookMeta.copyBook(): ItemStack {
        val bookMeta = Bukkit.getItemFactory().getItemMeta(Material.WRITABLE_BOOK) as BookMeta
        bookMeta.pages = this.pages
        val book = ItemStack(Material.WRITABLE_BOOK)
        book.itemMeta = bookMeta
        return book
    }

    /**
     * 导出书到文件
     * @param path 要导出的书的路径
     */
    fun BookMeta.exportBook(path: String) = File(path).writeText(JSONArray.toJSONString(this.pages))

    /**
     * 从文件导入书
     * @param path 要从哪个路径导入
     */
    fun importBook(path: String, type: Material = Material.WRITABLE_BOOK) = makeBook(File(path).readText(), type)

    /**
     * 使用字符串创建一个书与笔
     * @param text 书的内容
     */
    fun makeBook(text: String, type: Material = Material.WRITABLE_BOOK): ItemStack {
        val list = JSONParser().parse(text) as List<String>
        val bookMeta = Bukkit.getItemFactory().getItemMeta(type) as BookMeta
        bookMeta.pages = list
        val book = ItemStack(type)
        if(type==Material.WRITTEN_BOOK){
            bookMeta.title="Viewing for BookEdit"
            bookMeta.author="BookEdit by NekokeCore"
        }
        book.itemMeta = bookMeta
        return book
    }

}