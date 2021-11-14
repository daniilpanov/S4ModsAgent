package com.mymix.s4mods_agentv3.udd

import java.util

class Properties (var `type`: String, var name: String)
{
    private val props = new util.HashMap[String, String]()

    def get(prop_name: String): String =
        if (props.containsKey(prop_name))
            props.get(prop_name)
        else
            null

    def set(prop_name: String, prop_val: String): Unit =
        if (props.containsKey(prop_name))
            props.replace(prop_name, prop_val)
        else
            props.put(prop_name, prop_val)

    def del(prop_name: String): Unit =
        if (props.containsKey(prop_name))
            props.remove(prop_name)
}
