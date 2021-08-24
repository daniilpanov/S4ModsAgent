package com.mymix.s4mods_agentv3.controllers

import com.mymix.s4mods_agentv3.Constants
import com.mymix.s4mods_agentv3.models.{CachedOnlineMods, CategoriesCollection, Category, InstalledMods}
import org.jsoup.Jsoup
import org.jsoup.select.Elements

object ModsController
{
    var path: String = null

    // Инициализируем все управляемые контроллеры
    def init(): Unit =
    {
        ModsOnlineController.init()
        ModsInstalledController.init()
    }

    // Получение списка всех модов
    def getOnlineModsList(): CachedOnlineMods =
    {
        println(path)
        if (path != null)
            ModsOnlineController.getModsWithFilters(path)
        else
        {
            val r: Runnable = () => ModsOnlineController.updateCache()
            val m = ModsOnlineController.getModsFromCache()
            if (m.isEmpty)
                r.run()
            else
            {
                val bg_updating = new Thread(r)
                bg_updating.setPriority(Thread.MIN_PRIORITY)
                bg_updating.start()
            }
            m
        }
    }

    def getInstalledModsList(): InstalledMods =
    {
        val r: Runnable = () => ModsInstalledController.updateCache()
        val m = ModsInstalledController.getInstalledMods()
        if (m.isEmpty)
            r.run()
        else
        {
            val bg_updating = new Thread(r)
            bg_updating.setPriority(Thread.MIN_PRIORITY)
            bg_updating.start()
        }
        m
    }

    // Получение фильтров
    def getFilters(): CategoriesCollection =
    {
        val r: Runnable = () => updateFilters()
        val list = ModsInfoController.getCategories()
        if (list.isEmpty)
            r.run()
        else
        {
            val bg_updating = new Thread(r)
            bg_updating.setPriority(Thread.MIN_PRIORITY)
            bg_updating.start()
        }
        list
    }

    def updateFilters(): Unit =
    {
        val doc = Jsoup.connect(Constants.URL).get()
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
