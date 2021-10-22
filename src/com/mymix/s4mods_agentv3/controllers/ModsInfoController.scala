package com.mymix.s4mods_agentv3.controllers

import java.io.{File, FileInputStream, FileWriter, InputStreamReader}
import java.util

import com.google.gson.stream.JsonReader
import com.google.gson.{Gson, GsonBuilder}
import com.mymix.s4mods_agentv3.models._

object ModsInfoController
{
    var info_file_model: ModsInfoFile = _
    val builder = new GsonBuilder
    var gson: Gson = _

    def init(): Unit =
    {

        // Инициализируем Json-файл
        if (!new File(getClass.getResource("res/data.json").toURI).exists())
        {
            val file = new FileWriter(new File(getClass.getResource("res/data.json").toURI), false)
            file.append("{\n  \"cached_categories\": [],\n  \"installed_mods\": []\n}")
            file.close()
        }

        //val stream_reader = new FileInputStream("res/data.json")
        val stream_reader = getClass.getResourceAsStream("res/data.json").asInstanceOf[FileInputStream]

        builder.setPrettyPrinting()
        gson = builder.create()
        info_file_model = gson.fromJson(
            new JsonReader(new InputStreamReader(stream_reader)),
            classOf[ModsInfoFile]
        )
    }

    def getEaHome(): String = info_file_model.ea_home
    def getCategories(): CategoriesCollection = info_file_model.cached_categories
    def getInstalledMods(): InstalledMods = info_file_model.installed_mods

    def updateCategories(categories: CategoriesCollection): Unit = info_file_model.cached_categories = categories

    def addInstalledMod(new_mod: Mod): Unit =
    {
        if (!info_file_model.installed_mods.contains(new_mod))
            info_file_model.installed_mods.add(new_mod)
    }

    def addInstalledModsList(new_mods: util.List[Mod]): Unit =
    {
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

    // Сохранение
    def saveInfoFile(): Unit =
    {
        //val writer = new FileWriter("res/data.json")
        val writer = new FileWriter(new File(getClass.getResource("res/data.json").toURI))
        gson.toJson(info_file_model, writer)
        writer.close()
    }
}
