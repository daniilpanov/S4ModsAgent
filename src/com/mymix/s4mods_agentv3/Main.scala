package com.mymix.s4mods_agentv3

import java.awt._
import java.net.{HttpURLConnection, URL}

import com.mymix.s4mods_agentv3.activities.{Activity, OnlineModsListActivity, StartActivity}
import com.mymix.s4mods_agentv3.controllers._
import com.mymix.s4mods_agentv3.models.{Mod, ModInstaller}
import javax.swing.{JFrame, JLabel, JPanel, WindowConstants}

object Main extends JFrame
{
    var current_activity: Activity = null
    var internet_connection: Boolean = false
    val main_panel = new JPanel(new GridLayout(1, 1))

    def main(args: Array[String]): Unit =
    {
        checkInternetConnection()
        ModsInfoController.init()
        Constants.init()
        ModsController.init()

        setTitle("Sims 4 Mods Agent")
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        setSize(1000, 700)
        setMinimumSize(new Dimension(800, 600))
        UIDecorator.setCenteredWindow(this)
        setLayout(null)
        add(main_panel)
        //main_panel.add(new JLabel("OK!"))
        main_panel.setLocation(1, 1)
        main_panel.setSize(getSize)

        activity(new StartActivity)

        setVisible(true)
    }

    def activity(activity: Activity): Unit =
    {
        val pre_activity = current_activity
        current_activity = activity
        val ir = activity.init()
        if (ir != 0)
        {
            current_activity = pre_activity
            activity.initError(ir)
        }
        else
        {
            if (pre_activity != null)
                pre_activity.setInactive(main_panel)
            activity.setBackground(new Color(0, 0, 0, 0))
            activity.setActive(main_panel)
            getContentPane.validate()
            getContentPane.repaint()
        }
    }

    def install(mod: Mod, rootPanel: JPanel): ModInstaller =
    {
        if ("".equals(mod.download_link))
            ModsOnlineController.traceDownloadLink(mod)

        DownloadingManager.addInstallingTask(mod, rootPanel, current_activity.asInstanceOf[OnlineModsListActivity])
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

    def getThis(): JFrame = this


    def checkInternetConnection(): Unit =
    {
        internet_connection = false
        var con: HttpURLConnection = null
        try
        {
            con = new URL(Constants.URL).openConnection.asInstanceOf[HttpURLConnection]
            con.setRequestMethod("HEAD")
            internet_connection = con.getResponseCode == HttpURLConnection.HTTP_OK
        }
        catch
        {
            case _: Exception => internet_connection = false
        }
        finally
            if (con != null)
                try
                    con.disconnect()
                catch
                {
                    case _: Exception => internet_connection = false
                }
    }
}
