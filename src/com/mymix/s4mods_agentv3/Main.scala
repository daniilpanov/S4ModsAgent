package com.mymix.s4mods_agentv3

import java.awt.{GridLayout, Toolkit}
import java.io.File

import com.mymix.s4mods_agentv3.activities.{Activity, OnlineModsListActivity, StartActivity}
import com.mymix.s4mods_agentv3.controllers.{DownloadingManager, ModsController, ModsInstalledController}
import com.mymix.s4mods_agentv3.models.Mod
import javax.swing.{JFrame, WindowConstants}

object Main extends JFrame
{
    var current_activity: Activity = null

    def main(args: Array[String]): Unit =
    {
        Constants.init()
        ModsController.init()

        setTitle("Sims 4 Mods Agent")
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        val d = Toolkit.getDefaultToolkit.getScreenSize
        setLocation(((d.getWidth - 1000) / 2).toInt, ((d.getHeight - 700) / 2).toInt)
        setSize(1000, 700)
        setLayout(new GridLayout(1, 1))

        activity(new StartActivity)

        setVisible(true)
    }

    def activity(activity: Activity): Unit =
    {
        if (current_activity != null)
            current_activity.setInactive(getContentPane)
        current_activity = activity
        activity.init()
        activity.setActive(getContentPane)
        getContentPane.validate()
        getContentPane.repaint()
    }

    def install(mod: Mod): Unit =
    {
        DownloadingManager.addInstallingTask(mod)
    }

    def delete(mod: Mod): Unit =
    {
        ModsInstalledController.deleteMod(mod)
    }

    def error(code: Int): Unit =
    {
        println("ERROR! Code " + code.toString)
    }
}
