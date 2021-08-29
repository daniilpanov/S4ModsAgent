package com.mymix.s4mods_agentv3.models

class Mod(var name: String, var description: String, var link: String)
{
    var image: String = ""
    var download_link: String = ""
    var filename: String = ""
    var installed: Boolean = false
    var disabled: Boolean = true

    def this(name: String, description: String, link: String, image: String)
    {
        this(name, description, link)
        this.image = image
        if ("" == description)
            this.description = "Описание не найдено"
    }

    def == (mod: Mod): Boolean =
    {
        (mod.name == name &&
            mod.link == link && mod.image == image) ||
            (mod.filename == filename && filename != "")
    }

    override def equals(obj: Any): Boolean =
    {
        val mod = obj.asInstanceOf[Mod]
        mod == this
    }

    override def toString: String =
    {
        "{\"name\": \"" + name + "\", \"link\": \"" + link + "\", \"disabled\": " +
            disabled + "\", \"installed\": " + installed + "\", \"filename\": \"" + filename + "\"}"
    }
}
