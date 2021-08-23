package com.mymix.s4mods_agentv3.models

import java.io.{BufferedInputStream, FileOutputStream}
import java.net.{HttpURLConnection, URL}

import com.mymix.s4mods_agentv3.Constants
import com.mymix.s4mods_agentv3.controllers.ModsInfoController
import javax.swing.JProgressBar

class ModInstaller(val installing_mod: Mod) extends JProgressBar
{
    def beginLoading(): Unit =
    {
        val url = new URL(com.mymix.s4mods_agentv2.Constants.URL + installing_mod.download_link)
        val httpConnection = url.openConnection.asInstanceOf[HttpURLConnection]

        val completeFileSize = httpConnection.getContentLength
        setMinimum(0)
        setMaximum(completeFileSize)

        val fname = httpConnection.getHeaderField("Content-Disposition")
            .replace("attachment; filename=", "")
        installing_mod.filename = fname

        val in = new BufferedInputStream(httpConnection.getInputStream)
        val out = new FileOutputStream(Constants.sims_home + "Mods\\" + fname)

        println("Downloading...")

        val buffer = new Array[Byte](1024)
        var count = in.read(buffer)
        while (count != -1)
        {
            if (getValue + 1024 <= getMaximum)
                setValue(getValue + 1024)
            else
                setValue(getMaximum)

            out.write(buffer, 0, count)
            count = in.read(buffer)
        }

        in.close()
        out.close()

        installing_mod.installed = true
        ModsInfoController.addInstalledMod(installing_mod)
    }

    def end(): Unit =
    {
        try
        {
            getParent.remove(this)
        }
        catch
        {
            case ex: NullPointerException => println("Downloaded Successful!")
        }
    }
}
