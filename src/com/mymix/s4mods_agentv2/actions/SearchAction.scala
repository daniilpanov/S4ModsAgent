package com.mymix.s4mods_agentv2.actions

import java.io.IOException
import java.util

import com.mymix.s4mods_agentv2._
import com.mymix.s4mods_agentv2.controllers.CacheManager
import com.mymix.s4mods_agentv2.models.Mod
import javax.swing.JPanel
import org.jsoup.Jsoup


class SearchAction(var search: String = null, var category: String = null) extends Action
{
    private var mods: util.List[Mod] = new util.ArrayList[Mod]
    private var mods_blocks: util.List[JPanel] = new util.ArrayList[JPanel]

    override def init(): Unit =
    {
        if (CacheManager.cacheFile.CacheSw > 0)
        {
            mods = CacheManager.getMods()
            updateModsList()
            Main.bt.addTask(() => this.getMods(), false)
        }
        else
            getMods()
    }

    private def getMods(): Unit =
    {
        try
        {
            var url = Constants.URL
            if (search != null)
                url += "/search/?q=" + search
            val doc = Jsoup.connect(url).get
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
                    mods add newmod
                    browseDownloadLink(newmod)
                }
            })
            if (CacheManager.cacheFile.CacheSw > 1)
                CacheManager addModsList mods
        }
        catch
        {
            case ex: IOException => ex.printStackTrace()
        }
        finally
            updateModsList()
    }

    private def browseDownloadLink(mod: Mod): Unit =
    {
        Main.bt.addTask(() =>
        {
            val download_doc = Jsoup.connect(Constants.URL + mod.link).get
            val download_button = download_doc selectFirst "div.new-download > a.new_button"
            mod.download_link = download_button.attr("href")
            CacheManager.save()
            updateModsList()
        }, false)
    }

    def updateModsList(): Unit =
    {
        //mods.forEach()
    }
}
