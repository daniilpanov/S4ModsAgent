package com.mymix.s4mods_agentv3.controllers

import com.mymix.s4mods_agentv3.models.{CachedOnlineMods, CategoriesCollection, InstalledMods}

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
        ModsOnlineController.getMods()
    }

    def changePath(path: String = ""): Unit =
    {
        this.path = path
        ModsOnlineController.init()
    }

    def getInstalledModsList(): InstalledMods =
    {
        ModsInstalledController.updateCache()
        ModsInstalledController.getInstalledMods()
    }

    // Получение фильтров
    def getFilters(): CategoriesCollection =
    {
        val r: Runnable = () => ModsOnlineController.updateFilters()
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
}
