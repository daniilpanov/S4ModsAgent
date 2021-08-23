package com.mymix.s4mods_agentv2

import java.awt.{GridLayout, Toolkit}

import com.mymix.s4mods_agentv2.actions._
import com.mymix.s4mods_agentv2.controllers.{BackgroundTasks, CacheManager}
import javax.swing.{JFrame, JPanel, WindowConstants}

object Main extends JFrame
{
    private val start_action: Action = new StartAction
    private val wait_action = new WaitAction
    private var active_panel: JPanel = null

    val bt = new BackgroundTasks

    def main(args: Array[String]): Unit =
    {
        CacheManager.init()

        setTitle("Sims 4 Mods Agent")
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        val d = Toolkit.getDefaultToolkit.getScreenSize
        setLocation(((d.getWidth - 1000) / 2).toInt, ((d.getHeight - 700) / 2).toInt)
        setSize(1000, 700)
        setLayout(new GridLayout(1, 1))

        startAction()

        setVisible(true)
    }

    def setPanel(act: Action, wait: Boolean = false): Unit =
    {
        if (active_panel != null)
            getContentPane remove active_panel

        if (wait)
        {
            bt.addTask(() => {
                println("okokok")
                act.init()
                wait_action.stopWait()
                Main.getContentPane.remove(wait_action)
                Main.getContentPane.add(act)
                repaint()
                validate()
            }, false)
            validate()
            getContentPane add wait_action
            wait_action.startWait()
            repaint()
            validate()
        }
        else
        {
            getContentPane add act
            act.init()
        }

        active_panel = act

        repaint()
        validate()
    }

    def startAction(): Unit = setPanel(start_action)

    def searchAction(sq: String = null, category: String = null): Unit = setPanel(new SearchAction(sq), wait = true)

    def installedManagerAction(): Unit = setPanel(new InstalledManagerAction)
}
