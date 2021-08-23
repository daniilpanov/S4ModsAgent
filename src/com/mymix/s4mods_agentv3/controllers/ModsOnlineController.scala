package com.mymix.s4mods_agentv3.controllers

import java.io.IOException
import java.net.ConnectException

import com.mymix.s4mods_agentv3.Constants
import com.mymix.s4mods_agentv3.models.{CachedOnlineMods, Mod, ModsCollection}
import javax.swing.Timer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object ModsOnlineController
{
    var first_update = true
    var mods_updated: ModsCollection = new CachedOnlineMods
    var mods_updating_done: Boolean = false
    var doc: Document = _
    var pagination_max = 0
    var pagination_current = 0

    def init(): Unit =
    {
        doc = Jsoup.connect(Constants.URL + (if (null != ModsController.path) ModsController.path else "")).get()
        val t = new Timer(2500, _ => writeModsToCache())
        t.start()
    }

    def writeModsToCache(): Unit =
    {
        if (mods_updating_done)
        {
            ModsInfoController.addOnlineModsList(mods_updated, first_update)
            mods_updated.clear()
            mods_updating_done = false
        }

        first_update = false
    }

    def getModsFromCache(): CachedOnlineMods =
    {
        ModsInfoController.getOnlineMods()
    }

    def getModsWithFilters(path: String = ""): CachedOnlineMods =
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

    def updateCache(): Unit =
    {
        if (mods_updating_done)
            return

        try
        {
            mods_updated.addAll(getModsWithFilters())
            mods_updating_done = true
        }
        catch
        {
            case ex: IOException => ex.printStackTrace()
            case ex: ConnectException => ex.printStackTrace()
        }
    }

    def browseDownloadLink(mod: Mod): Unit =
    {
        val download_doc = Jsoup.connect(Constants.URL + mod.link).get
        val download_button = download_doc selectFirst "div.new-download > a.new_button"
        mod.download_link = download_button attr "href"
        val description = download_doc selectFirst "div.material-description"
        mod.description = description.html().replace("<br>", "\n")
        ModsInfoController.saveInfoFile()
    }


    def getPagination(): Int =
    {
        if (pagination_max > 0)
            return pagination_max

        val links: Elements = doc select ".pagination-block .page-link"
        pagination_current = doc.selectFirst(".pagination-block .page-link-selected").html().toInt
        pagination_max = Math.max(
            links.last().html().toInt,
            pagination_current
        )
        pagination_max
    }
}
