package com.mymix.s4mods_agentv3.controllers

import java.util

import com.mymix.s4mods_agentv3.models.{Mod, ModInstaller}
import javax.swing.JPanel

object DownloadingManager
{
    val tasks: util.List[ModInstaller] = new util.ArrayList[ModInstaller]()

    def addInstallingTask(mod: Mod, rootPanel: JPanel): ModInstaller =
    {
        val task = new ModInstaller(mod, rootPanel)
        tasks.add(task)
        var loading: Thread = null
        loading = new Thread(() =>
        {
            task.beginLoading()
            ModsInfoController.saveInfoFile()
            task.end()
            tasks.remove(task)
            loading.interrupt()
        })
        loading.start()

        task
    }
}
