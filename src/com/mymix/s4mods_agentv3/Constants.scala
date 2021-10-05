package com.mymix.s4mods_agentv3

import java.io.File

import com.mymix.s4mods_agentv3.controllers.ModsInfoController
import javax.swing.UIManager

object Constants
{
    val URL = "https://sims4pack.ru"
    val max_cached_mods = 250

    var ea_home: String = System.getProperty("user.home") + "\\Documents\\Electronic Arts\\"
    var sims_home: String = ea_home

    def init(): Unit =
    {
        var homedir = new File(ea_home)

        if (!homedir.exists())
        {
            if (ModsInfoController.getEaHome() == "")
                Main.error(404)
            homedir = new File(ModsInfoController.getEaHome())
            if (!homedir.exists())
                Main.error(404)
            sims_home = ModsInfoController.getEaHome()
        }

        var sims_dirname = "The Sims 4"
        var found = false

        for (e <- homedir.listFiles())
        {
            if (e.getName == "The Sims 4 издание Legacy")
                sims_dirname = e.getName

            if (e.getName == sims_dirname)
                found = true
        }

        if (!found)
            Main.error(404)

        sims_home += sims_dirname + "\\"


        ///
        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        UIManager.put("OptionPane.cancelButtonText", "Отмена")
    }

    def log(p: Any): Unit = println(p)
}
