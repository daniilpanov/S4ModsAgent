package com.mymix.s4mods_agentv3.models

import java.io.{BufferedInputStream, FileOutputStream}
import java.net.{HttpURLConnection, URL}

import com.mymix.s4mods_agentv3.{Constants, Main}
import com.mymix.s4mods_agentv3.controllers.ModsInfoController
import javax.swing.{JPanel, JProgressBar}

import scala.util.control.Breaks._

class ModInstaller(val installing_mod: Mod) extends JProgressBar
{
    var done = false
    var panel = new JPanel()
    var root_panel: JPanel = _

    var on_pause = false
    var on_stop = false

    def this(installing_mod: Mod, rootPanel: JPanel)
    {
        this(installing_mod)
        root_panel = rootPanel
    }

    def makeVisible(): Unit =
    {
        //
        setString(installing_mod.name)
        setStringPainted(true)
        //
        panel.add(this)
        //
        root_panel.add(panel)
    }

    def beginLoading(): Unit =
    {
        makeVisible()
        //
        val url = new URL(com.mymix.s4mods_agentv3.Constants.URL + installing_mod.download_link)
        val httpConnection = url.openConnection.asInstanceOf[HttpURLConnection]
        //
        val completeFileSize = httpConnection.getContentLength
        setMinimum(0)
        setMaximum(completeFileSize)
        //
        val fname = httpConnection.getHeaderField("Content-Disposition")
            .replace("attachment; filename=", "")
        installing_mod.filename = fname
        //
        val in = new BufferedInputStream(httpConnection.getInputStream)
        val out = new FileOutputStream(Constants.sims_home + "Mods\\" + fname)

        println("Downloading...")
        //
        val buffer = new Array[Byte](1024)
        var count = in.read(buffer)
        //
        breakable
        {
            while (count != -1)
            {
                if (on_stop)
                {
                    Main.delete(installing_mod)
                    break()
                }
                if (!on_pause)
                {
                    //
                    if (getValue + 1024 <= getMaximum)
                        setValue(getValue + 1024)
                    else
                        setValue(getMaximum)

                    out.write(buffer, 0, count)
                    count = in.read(buffer)
                }
            }
        }
        //
        in.close()
        out.close()
        //
        installing_mod.installed = true
        installing_mod.disabled = false
        ModsInfoController.addInstalledMod(installing_mod)
    }

    def pause(): Unit = on_pause = true
    def resume(): Unit = on_pause = false
    def stop(): Unit = on_stop = true

    def end(): Unit =
    {
        //
        println("Downloaded Successful!")
        done = true
    }

    def remove(): Unit =
    {
        //
        root_panel.remove(panel)
    }
}

