package com.mymix.s4mods_agentv3.models

class Mod(var name: String, var description: String, var link: String, var image: String)
{
    var download_link: String = ""
    var filename: String = ""
    var installed: Boolean = false
    var disabled: Boolean = true

    def == (mod: Mod): Boolean =
    {
        (mod.download_link == download_link && mod.name == name &&
            mod.link == link && mod.description == description &&
            mod.image == image) || (mod.filename == filename && filename != "")
    }

    override def equals(obj: Any): Boolean =
    {
        val mod = obj.asInstanceOf[Mod]
        mod == this
    }
}
