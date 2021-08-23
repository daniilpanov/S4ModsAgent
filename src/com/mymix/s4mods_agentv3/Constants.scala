package com.mymix.s4mods_agentv3

import java.io.File

object Constants
{
    val URL = "https://sims4pack.ru"
    val ea_home: String = System.getProperty("user.home") + "\\Documents\\Electronic Arts\\"
    val max_cached_mods = 250

    var sims_home: String = ea_home

    def init(): Unit =
    {
        val homedir = new File(ea_home)

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
    }

    def log(p: Any): Unit = println(p)
}
