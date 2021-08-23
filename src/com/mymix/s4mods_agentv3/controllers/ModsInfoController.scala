package com.mymix.s4mods_agentv3.controllers

import java.io.{File, FileInputStream, FileWriter, InputStreamReader}
import java.util

import com.google.gson.stream.JsonReader
import com.google.gson.{Gson, GsonBuilder}
import com.mymix.s4mods_agentv3.Constants
import com.mymix.s4mods_agentv3.models._

object ModsInfoController
{
    var info_file_model: ModsInfoFile = _
    val builder = new GsonBuilder
    var gson: Gson = _

    def init(): Unit =
    {
        // Инициализируем Json-файл
        if (!new File("res/data.json").exists())
        {
            val file = new FileWriter("res/data.json", false)
            file.append("{\n  \"cached_mods\": [],\n  \"installed_mods\": []\n}")
            file.close()
        }

        val stream_reader = new FileInputStream("res/data.json")

        builder.setPrettyPrinting()
        gson = builder.create()
        info_file_model = gson.fromJson(
            new JsonReader(new InputStreamReader(stream_reader)),
            classOf[ModsInfoFile]
        )
    }

    def getCategories(): CategoriesCollection = info_file_model.cached_categories
    def getOnlineMods(): CachedOnlineMods = info_file_model.cached_mods
    def getInstalledMods(): InstalledMods = info_file_model.installed_mods

    def updateCategories(categories: CategoriesCollection): Unit = info_file_model.cached_categories = categories

    def addInstalledMod(new_mod: Mod): Unit =
    {
        if (!info_file_model.installed_mods.contains(new_mod))
            info_file_model.installed_mods.add(new_mod)
    }

    def addInstalledModsList(new_mods: util.List[Mod]): Unit =
    {
        println("Installed Mods Modified!")
        new_mods.forEach(mod =>
        {
            var add = true
            info_file_model.installed_mods.forEach(mod2 => {
                if (mod == mod2)
                    add = false
            })

            if (add)
                info_file_model.installed_mods.add(mod)
        })

        saveInfoFile()
    }

    def addOnlineMod(new_mod: Mod): Unit =
    {
        if (!info_file_model.cached_mods.contains(new_mod))
            info_file_model.cached_mods.add(new_mod)
    }

    private def reinit(new_mods: util.List[Mod]): Unit =
    {
        var tmp_mods: util.List[Mod] = info_file_model.cached_mods.clone().asInstanceOf[util.List[Mod]]
        info_file_model.cached_mods.clear()
        info_file_model.cached_mods.addAll(new_mods)

        tmp_mods.forEach(mod =>
        {
            if (!new_mods.contains(mod))
                info_file_model.cached_mods.add(mod)
        })
    }

    def addOnlineModsList(new_mods: util.List[Mod], load_before_all: Boolean = false): Unit =
    {
        if (info_file_model.cached_mods.size() < Constants.max_cached_mods)
        {
            if (load_before_all)
            {
                reinit(new_mods)
                return
            }

            new_mods.forEach(mod =>
            {
                var add = true
                info_file_model.cached_mods.forEach(mod2 => {
                    if (mod == mod2)
                        add = false
                })

                if (add)
                    info_file_model.cached_mods.add(mod)
            })
        }

        saveInfoFile()
    }

    // Сохранение
    def saveInfoFile(): Unit =
    {
        val writer = new FileWriter("res/data.json")
        gson.toJson(info_file_model, writer)
        writer.close()
    }
}
