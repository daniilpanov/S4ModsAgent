package com.mymix.s4mods_agentv3.controllers

import java.io.File
import java.nio.file.{Files, Paths}

import com.mymix.s4mods_agentv3.models.{InstalledMods, Mod}
import com.mymix.s4mods_agentv3.Constants

object ModsInstalledController
{
    var mods_dir: File = _
    var disabled_mods_dir: File = _

    def init(): Unit =
    {
        // Инициализируем объекты каталогов с модами
        mods_dir = new File(Constants.sims_home + "\\Mods")
        disabled_mods_dir = new File(Constants.sims_home + "\\ModsDisabled")
        if (!disabled_mods_dir.exists())
            Files.createDirectory(Paths.get(Constants.sims_home + "\\ModsDisabled"))

        updateCache()
    }

    def getInstalledMods(): InstalledMods =
    {
        ModsInfoController.getInstalledMods()
    }

    def updateCache(): Unit =
    {
        for (e <- mods_dir.list())
        {
            if (e != "Resource.cfg")
            {
                if (null == searchModByFilename(e))
                {
                    val m = new Mod(e, "Unknown", "", "")
                    m.filename = e
                    m.disabled = false
                    m.installed = true
                    ModsInfoController.addInstalledMod(m)
                }
            }
        }

        for (e <- disabled_mods_dir.list())
        {
            if (null == searchModByFilename(e))
            {
                val m = new Mod(e, "Unknown", "", "")
                m.filename = e
                m.disabled = true
                m.installed = true
                ModsInfoController.addInstalledMod(m)
            }
        }
    }

    def isModInstalled(mod: Mod): Boolean =
    {
        mod.installed = false
        ModsInfoController.getInstalledMods().forEach(e =>
        {
            if (e == mod && e.installed)
                mod.installed = true
        })

        mod.installed
    }

    def searchModByFilename(fname: String): Mod =
    {
        var mod: Mod = null
        ModsInfoController.getInstalledMods().forEach(e =>
        {
            if (e.filename == fname)
                mod = e
        })

        mod
    }

    def deleteMod(mod: Mod): Unit =
    {
        var f = new File(Constants.sims_home + "Mods\\" + mod.filename)
        if (!f.exists())
            f = new File(Constants.sims_home + "ModsDisabled\\" + mod.filename)
        f.delete()
        println(ModsInfoController.getInstalledMods().remove(mod))
        ModsInfoController.saveInfoFile()
    }

    def switchDisable(mod: Mod): Unit =
    {
        var a = new Array[String](2)
        if (mod.disabled)
        {
            a(0) = "ModsDisabled"
            a(1) = "Mods"
        }
        else
        {
            a(0) = "Mods"
            a(1) = "ModsDisabled"
        }
        val fromTo = a

        val from = Constants.sims_home + fromTo(0) + "\\" + mod.filename
        val to = Constants.sims_home + fromTo(1) + "\\" + mod.filename
        Files.move(Paths.get(from), Paths.get(to))

        mod.disabled = !mod.disabled
    }
}
