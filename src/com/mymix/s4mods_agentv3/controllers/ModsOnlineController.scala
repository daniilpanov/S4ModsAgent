package com.mymix.s4mods_agentv3.controllers

import java.io.IOException
import java.net.ConnectException

import com.mymix.s4mods_agentv3.Constants
import com.mymix.s4mods_agentv3.models.{CachedOnlineMods, CategoriesCollection, Category, Mod}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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


        doc = Jsoup.connect(url).get()
    }

    def getModsFromCache(): CachedOnlineMods = cached_mods

    def getMods(): CachedOnlineMods =
    {
        val list = new CachedOnlineMods
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
                    list add newmod
                    browseDownloadLink(newmod)
                }
            })
        }
        catch
        {
            case ex: IOException => ex.printStackTrace()
            case ex: ConnectException => ex.printStackTrace()
        }

        list
    }

    def browseDownloadLink(mod: Mod): Unit =
    {
        val download_doc = Jsoup.connect(Constants.URL + mod.link).get
        val download_button = download_doc selectFirst "div.new-download > a.new_button"
        mod.download_link = download_button attr "href"
        val description = download_doc selectFirst "div.material-description"
        mod.description = description.html().replace("<br>", "\n")
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

    def updateFilters(): Unit =
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
}
