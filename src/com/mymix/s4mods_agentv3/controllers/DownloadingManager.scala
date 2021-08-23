package com.mymix.s4mods_agentv3.controllers

import java.util

import com.mymix.s4mods_agentv3.models.{Mod, ModInstaller}

object DownloadingManager
{
    val tasks: util.List[ModInstaller] = new util.ArrayList[ModInstaller]()

    def addInstallingTask(mod: Mod): Unit =
    {
        val task = new ModInstaller(mod)
        tasks.add(task)
        val loading = new Thread(() =>
        {
            task.beginLoading()
            ModsInfoController.saveInfoFile()
            task.end()
            tasks.remove(task)
        })
        loading.start()
    }
}
