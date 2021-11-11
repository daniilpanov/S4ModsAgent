package com.mymix.s4mods_agentv3.models

import java.io.{BufferedInputStream, FileInputStream, FileOutputStream}
import java.net.{HttpURLConnection, URL}
import java.nio.file.{Files, Paths}
import java.util
import java.util.zip.{ZipEntry, ZipInputStream}

import com.mymix.s4mods_agentv3.activities.OnlineModsListActivity
import com.mymix.s4mods_agentv3.{Constants, Images, Main, UIDecorator}
import com.mymix.s4mods_agentv3.controllers.ModsInfoController
import javax.swing._

class ModInstaller(val installing_mod: Mod, val root_panel: JPanel) extends JProgressBar
{
    var activity: OnlineModsListActivity = _

    var done = false
    var panel = new JPanel()
    val mod_name = new JLabel()
    val stop_close = new JButton()
    val pause_resume = new JButton()
    val group_layout = new GroupLayout(panel)

    var on_pause = false
    var on_stop = false

    // плавно изменяем цвет с красного на зелёный
    private var r = 255
    private var g = 0

    private val interrupted: util.List[Runnable] = new util.ArrayList[Runnable]()
    private val installed: util.List[Runnable] = new util.ArrayList[Runnable]()

    def this(installing_mod: Mod, rootPanel: JPanel, activity: OnlineModsListActivity)
    {
        this(installing_mod, rootPanel)
        this.activity = activity

        panel.setLayout(group_layout)

        mod_name.setText(installing_mod.name)

        UIDecorator.makeIconButton(stop_close, Images.bstop, 20, 20)
        UIDecorator.makeIconButton(pause_resume, Images.bpause, 20, 20)
        pause_resume setDisabledIcon UIDecorator.getScaledImageIcon(Images.bdpause, 20, 20)
        pause_resume.addActionListener(_ =>
        {
            if (on_pause)
                resume()
            else
                pause()
        })

        stop_close.addActionListener(_ =>
        {
            if (on_stop)
                remove()
            else
                stop()
        })
    }

    def interrupted(r: Runnable): ModInstaller =
    {
        interrupted add r
        this
    }

    def installed(r: Runnable): ModInstaller =
    {
        installed add r
        this
    }

    def makeVisible(): Unit =
    {
        //
        setString("0%")
        setStringPainted(true)
        //
        group_layout.setHorizontalGroup(
            group_layout.createSequentialGroup().addGroup(
                group_layout.createParallelGroup().addComponent(stop_close)
                    .addComponent(pause_resume)
            ).addGroup(
                group_layout.createParallelGroup().addComponent(mod_name)
                    .addComponent(this)
            )
        )

        group_layout.setVerticalGroup(
            group_layout.createSequentialGroup().addGroup(
                group_layout.createParallelGroup().addComponent(stop_close)
                    .addComponent(mod_name)
            ).addGroup(
                group_layout.createParallelGroup().addComponent(pause_resume)
                    .addComponent(this)
            )
        )
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
        val prop: Double = 100D / completeFileSize * 1024
        var current_progress: Double = 0
        //
        val fname = httpConnection.getHeaderField("Content-Disposition")
            .replace("attachment; filename=", "")
        installing_mod.filename = fname
        //
        val in = new BufferedInputStream(httpConnection.getInputStream)
        val p = Constants.sims_home + "Mods\\" + fname
        val out = new FileOutputStream(p)

        //
        val buffer = new Array[Byte](1024)
        var count = in.read(buffer)
        //
        while (count != -1 && !on_stop)
        {
            if (!on_pause)
            {
                //
                if (getValue + 1024 <= getMaximum)
                    setValue(getValue + 1024)
                else
                    setValue(getMaximum)

                out.write(buffer, 0, count)
                count = in.read(buffer)
                current_progress += prop
                setString(current_progress.toInt.toString + "%")
            }
            else
                Thread.currentThread().join(1)
        }
        //
        in.close()
        out.close()
        if (fname.endsWith(".zip") && !on_stop)
        {
            installing_mod.multiple = true
            try
            {
                setString("Распаковка...")
                stop_close.setEnabled(false)
                pause_resume.setEnabled(false)

                val zin = new ZipInputStream(new FileInputStream(p))
                val checkboxes = new util.ArrayList[JCheckBox]()
                var entry: ZipEntry = zin.getNextEntry
                var name: String = null
                val dialog = new JDialog(Main.getThis(),
                    "Все ли моды нужны?", true)
                dialog.setSize(400, 800)
                dialog.getContentPane.setLayout(new BoxLayout(dialog.getContentPane, BoxLayout.Y_AXIS))

                while (entry != null)
                {
                    name = entry.getName
                    val cb = new JCheckBox(name, true)
                    dialog.getContentPane.add(cb)
                    checkboxes.add(cb)

                    val fout = new FileOutputStream(Constants.sims_home + "Mods\\" + name)
                    var c = zin.read()
                    while (c != -1)
                    {
                        fout.write(c)
                        c = zin.read()
                    }

                    fout.flush()
                    zin.closeEntry()
                    fout.close()
                    entry = zin.getNextEntry
                }

                val continue = new JButton("Готово")
                val files = new util.ArrayList[String]()
                continue.addActionListener(_ =>
                {
                    checkboxes.forEach(e =>
                    {
                        if (!e.isSelected)
                        {
                            Files.delete(Paths.get(Constants.sims_home + "Mods\\" + e.getText))
                        }
                        else
                        {
                            files.add(e.getText)
                        }
                    })
                    stop_close.setEnabled(true)
                    pause_resume.setEnabled(true)
                    installing_mod.filename = String.join("|", files)
                    dialog.dispose()
                })
                dialog.getContentPane.add(continue)

                dialog.setVisible(true)
                zin.close()
                Files.delete(Paths.get(p))
            }
            catch
            {
                case ex: Exception => println(ex.getMessage)
            }
        }
        //
        if (on_stop)
        {
            Main.delete(installing_mod)
            remove()
        }
        else
        {
            //
            installing_mod.installed = true
            installing_mod.disabled = false
            ModsInfoController.addInstalledMod(installing_mod)
            setString("Готово!")
        }
    }

    def pause(): Unit =
    {
        on_pause = true
        UIDecorator.makeIconButton(pause_resume, Images.bresume, 20, 20)
        pause_resume setDisabledIcon UIDecorator.getScaledImageIcon(Images.bdresume, 20, 20)
    }

    def resume(): Unit =
    {
        on_pause = false
        UIDecorator.makeIconButton(pause_resume, Images.bpause, 20, 20)
        pause_resume setDisabledIcon UIDecorator.getScaledImageIcon(Images.bdpause, 20, 20)
    }

    def stop(): Unit =
    {
        on_stop = true
        pause_resume setEnabled false
        UIDecorator.makeIconButton(stop_close, Images.btimes, 20, 20)
        activity.endDownloading(this)
    }

    def end(): Unit =
    {
        stop()
        //
        done = true

        installed.forEach(e => e.run())
    }

    def remove(): Unit =
    {
        //
        root_panel.remove(panel)
        activity.removeDownloading(this)
    }
}

