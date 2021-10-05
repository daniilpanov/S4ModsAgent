package com.mymix.s4mods_agentv3.controllers

import java.io.IOException
import java.net.{ConnectException, UnknownHostException}
import java.util
import java.util.ConcurrentModificationException

import com.mymix.s4mods_agentv3.activities.{InstalledModsListActivity, OnlineModsListActivity}
import com.mymix.s4mods_agentv3.models.{CachedOnlineMods, CategoriesCollection, Category, Mod}
import com.mymix.s4mods_agentv3.{Constants, Main}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

object ModsOnlineController
{
    var first_update = true
    var mods_updated: CachedOnlineMods = new CachedOnlineMods
    var cached_mods: CachedOnlineMods = new CachedOnlineMods
    var mods_updating_done: Boolean = false
    var doc: Document = _
    var pagination_max = 0
    var pagination_current = 1

    var th_image_loading: Thread = _
    var th_info_loading: Thread = _
    var loaded_full_info: util.List[Array[String]] = new util.ArrayList[Array[String]]()
    var loading: Boolean = false

    def init(): Unit =
    {
        var url = Constants.URL
        if (null != ModsController.path)
        {
            url += ModsController.path
            if (ModsController.page > 1)
                url += (if (ModsController.path.matches("^/search\\?q=")) "&" else "?") +
                    "page=" + ModsController.page.toString
        }
        else if (ModsController.page > 1)
            url += "?page=" + ModsController.page.toString


        try
            doc = Jsoup.connect(url).get()
        catch
        {
            case _: Exception => Main.internet_connection = false
        }
    }

    def startBGLoading(): Unit =
    {
        stopBGLoading()
        loading = true

        th_info_loading = new Thread(() =>
        {
            try
            {
                if (loading)
                {
                    cached_mods.forEach(el =>
                    {
                        if (loading)
                        {
                            var tdl: Array[String] = traceDownloadLink(el)
                            loaded_full_info.add(tdl)
                            Main.current_activity.asInstanceOf[OnlineModsListActivity]
                                .updateDLnDesc(el.link, tdl(1), tdl(0))
                        }
                    })
                }
            }
            catch
            {
                case _: ConcurrentModificationException =>
                case _: NullPointerException =>
            }
        })
        th_info_loading.start()

        th_image_loading = new Thread(() =>
        {
            try
            {
                if (loading)
                {
                    cached_mods.forEach(el =>
                    {
                        if (loading)
                        {
                            Main.current_activity.asInstanceOf[OnlineModsListActivity].updateImage(el.link, el.image)
                            Main.current_activity.repaint()
                        }
                    })
                }
            }
            catch
            {
                case _: ConcurrentModificationException =>
                case _: NullPointerException =>
            }
        })
        th_image_loading.start()
    }

    def stopBGLoading(): Unit =
    {
        loading = false

        if (null != th_image_loading)
        {
            th_image_loading.interrupt()
            th_image_loading = null
        }
        if (null != th_info_loading)
        {
            th_info_loading.interrupt()
            th_info_loading = null
        }
    }

    def getModsFromCache(): CachedOnlineMods = cached_mods

    def getMods(): CachedOnlineMods =
    {
        cached_mods.clear()
        try
        {
            val mod_list = doc select "div.grid-block"

            mod_list.forEach(e => {
                val desc = e selectFirst "div.grid-desc"
                val link = e selectFirst "a.grid-link"
                val image = e selectFirst "div.grid-image"
                if (desc != null && link != null && image != null)
                {
                    val img_url = image.attr("style")
                        .replace("background-image:url('", "")
                        .replace("');", "")
                    val newmod = new Mod(link.text, desc.html, link attr "href", img_url)
                    cached_mods add newmod
                }
            })
        }
        catch
        {
            case ex: IOException => ex.printStackTrace()
            case ex: ConnectException => ex.printStackTrace()
        }

        cached_mods
    }

    def traceDownloadLink(mod: Mod): Array[String] =
    {
        val download_doc = Jsoup.connect(Constants.URL + mod.link).get
        val download_button = download_doc selectFirst "div.new-download > a.new_button"
        val description = download_doc selectFirst "div.material-description"

        var desc = description.html().replace("<br>", "\n")
        desc = desc.split(" <!-- VK Widget --> ")(0)
        mod.description = desc
        val dl = download_button attr "href"
        mod.download_link = dl

        Array(dl.toString, desc)
    }

    def getPagination(): Int =
    {
        val links: Elements = doc select ".pagination-block .page-link"
        pagination_current = doc.selectFirst(".pagination-block .page-link-selected").html().toInt
        pagination_max = Math.max(
            links.last().html().toInt,
            pagination_current
        )
        pagination_max
    }

    def addOnlineMod(new_mod: Mod): Unit =
    {
        if (!cached_mods.contains(new_mod))
            cached_mods.add(new_mod)
    }

    def addOnlineModsList(mods_list: CachedOnlineMods): Unit = cached_mods.addAll(mods_list)

    def updateFilters(): Unit = if (Main.internet_connection)
    {
        val categories_list = doc select ".menu-block > .menu-links li"
        var links: Elements = null
        val collection = new CategoriesCollection
        var ID = 1

        categories_list.forEach(e =>
        {
            links = e select "a"
            collection.add(new Category(ID, links.get(0).html(), links.get(0).attr("href")))

            if (links.size() > 1)
            {
                val parent_id = ID
                links.forEach(e =>
                {
                    if (ID > parent_id)
                    {
                        collection.add(new Category(ID, e.html(), e.attr("href"), parent_id))
                    }

                    ID += 1
                })

                ID -= 1
            }

            ID += 1
        })

        ModsInfoController.updateCategories(collection)
        ModsInfoController.saveInfoFile()
    }

    def getSliderImagesLink(mod_link: String): util.List[String] =
    {
        val img_list = new util.ArrayList[String]()
        val doc = Jsoup.connect(Constants.URL + mod_link).get()
        val imgs: Elements = doc select ".material-screenshots .material-screenshots-image"

        imgs.forEach(el =>
        {
            var link = el attr "style"
            link = link.replace("background-image: url('", "")
            link = link.replace("');", "")
            img_list.add(link)
        })

        if (img_list.isEmpty)
        {
            val img: Element = doc selectFirst ".material-image"
            img_list.add(img attr "src")
        }

        img_list
    }
}
