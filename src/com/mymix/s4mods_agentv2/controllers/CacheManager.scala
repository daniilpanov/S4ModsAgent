package com.mymix.s4mods_agentv2.controllers

import java.io._
import java.util

import com.google.gson.stream.JsonReader
import com.google.gson.{Gson, GsonBuilder}
import com.mymix.s4mods_agentv2.Constants
import com.mymix.s4mods_agentv2.models.{CacheFile, Mod}

object CacheManager
{
    // 0 - не кэшировать, 1 - использовать накопившийся кэш, 2 - кэширование работает

    var cacheFile: CacheFile = _
    val builder = new GsonBuilder
    var gson: Gson = _

    def init(): Unit =
    {
        var fis: InputStream = null
        try
        {
            fis = new FileInputStream("res/cache.json")
        }
        catch
        {
            case _: FileNotFoundException =>
                val file = new FileWriter("res/cache.json", false)
                file.append("{\n  \"CacheSw\": 0,\n  \"mods\": []\n}")
                file.close()
                fis = new FileInputStream("res/cache.json")
        }

        builder.setPrettyPrinting()
        gson = builder.create()
        cacheFile = gson.fromJson(
            new JsonReader(new InputStreamReader(fis)),
            classOf[CacheFile]
        )
    }

    def cachingSwitch(sw: Int): Unit =
    {
        cacheFile.CacheSw = sw
    }

    def getMods(): util.List[Mod] = cacheFile.mods

    def addModsList(mods: util.List[Mod]): Unit =
    {
        println("Mods Cache Modified!")
        if (cacheFile.mods.size() < Constants.max_cached_mods)
        {
            mods.forEach(mod =>
            {
                var add = true
                cacheFile.mods.forEach(mod2 => {
                    if (mod == mod2)
                        add = false
                })

                if (add)
                    cacheFile.mods.add(mod)
            })
        }

        save()
    }

    def save(): Unit =
    {
        val writer = new FileWriter("res/cache.json")
        gson.toJson(cacheFile, writer)
        writer.close()
    }
}
