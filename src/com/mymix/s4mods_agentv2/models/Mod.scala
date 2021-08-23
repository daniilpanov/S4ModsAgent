package com.mymix.s4mods_agentv2.models

class Mod (var name: String, var description: String, var link: String, var image: String)
{
    var download_link = ""
    // 0 - не установлен, 1 - деактивирован, 2 - активен
    var installed = 0

    def == (mod: Mod): Boolean = link == mod.link
}
