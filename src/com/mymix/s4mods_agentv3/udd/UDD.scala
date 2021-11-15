package com.mymix.s4mods_agentv3.udd

import java.awt.Color
import java.util

object UDD
{
    private val classes: util.List[aClass] = new util.ArrayList()

    def addClass(new_class: aClass): Boolean =
    {
        if (null == getClass(new_class.getName))
        {
            classes.add(new_class)
            return true
        }
        false
    }

    def deleteClass(classname: String): Unit =
    {
        var i = 0
        for (i: Int <- 0 to classes.size)
        {
            if (classname == classes.get(i).getName)
                classes.remove(i)
        }
    }

    def getClass(classname: String): aClass =
    {
        var res: aClass = null

        classes.forEach(cl =>
        {
            if (classname == cl.getName)
                res = cl
        })

        res
    }

    // Вспомогательные функции
    def getColorCode(c: Color): Int =
        ((c.getRed & 0xFF) << 16) | ((c.getGreen & 0xFF) << 8) | ((c.getBlue & 0xFF) << 0)
}
