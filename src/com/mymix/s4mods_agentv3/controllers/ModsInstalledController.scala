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
            if (e == mod)
            {
                mod.installed = true
                mod.disabled = e.disabled
                mod.filename = e.filename
                mod.multiple = e.multiple
            }
        })

        mod.installed
    }

    def searchModByFilename(fname: String): Mod =
    {
        var mod: Mod = null
        ModsInfoController.getInstalledMods().forEach(e =>
        {
            if (e.filename.indexOf(fname) != -1)
                mod = e
        })

        mod
    }

    def deleteMod(mod: Mod): Unit =
    {
        println(mod.filename)
        println(mod.multiple)
        if (mod.multiple)
        {
            val files = mod.filename.split('|')
            for (file <- files)
            {
                var f = new File(Constants.sims_home + "Mods\\" + file)
                if (!f.exists())
                    f = new File(Constants.sims_home + "ModsDisabled\\" + file)
                f.delete()
            }
        }
        else
        {
            var f = new File(Constants.sims_home + "Mods\\" + mod.filename)
            if (!f.exists())
                f = new File(Constants.sims_home + "ModsDisabled\\" + mod.filename)
            f.delete()
        }

        ModsInfoController.getInstalledMods().remove(mod)
        ModsInfoController.saveInfoFile()
    }

    def switchDisable(mod: Mod): Unit =
    {
        val a = new Array[String](2)

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

        if (mod.multiple)
        {
            val files = mod.filename.split('|')
            for (file <- files)
            {
                val from = Constants.sims_home + fromTo(0) + "\\" + file
                val to = Constants.sims_home + fromTo(1) + "\\" + file
                Files.move(Paths.get(from), Paths.get(to))
            }
        }
        else
        {
            val from = Constants.sims_home + fromTo(0) + "\\" + mod.filename
            val to = Constants.sims_home + fromTo(1) + "\\" + mod.filename
            Files.move(Paths.get(from), Paths.get(to))
        }

        mod.disabled = !mod.disabled
    }
}
