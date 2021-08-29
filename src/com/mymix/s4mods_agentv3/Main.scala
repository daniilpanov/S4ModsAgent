package com.mymix.s4mods_agentv3

import java.awt.{GridLayout, Toolkit}

import com.mymix.s4mods_agentv3.activities.{Activity, StartActivity}
import com.mymix.s4mods_agentv3.controllers._
import com.mymix.s4mods_agentv3.models.{Mod, ModInstaller}
import javax.swing.{JFrame, JPanel, WindowConstants}

object Main extends JFrame
{
    var current_activity: Activity = null

    def main(args: Array[String]): Unit =
    {
        ModsInfoController.init()
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

    def install(mod: Mod, rootPanel: JPanel): ModInstaller =
    {
        if ("".equals(mod.download_link))
            ModsOnlineController.traceDownloadLink(mod)

        DownloadingManager.addInstallingTask(mod, rootPanel)
    }

    def delete(mod: Mod): Unit =
    {
        ModsInstalledController.deleteMod(mod)
    }

    def error(code: Int): Unit =
    {
        println("ERROR! Code " + code.toString)
        System.exit(0)
    }
}
