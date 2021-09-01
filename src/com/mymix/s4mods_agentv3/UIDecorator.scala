package com.mymix.s4mods_agentv3

import java.awt.{Cursor, Dimension, Image}
import java.net.{MalformedURLException, URL}

import javax.swing.{ImageIcon, JButton}

object UIDecorator
{
    def getScaledImageIcon(scaling: ImageIcon, width: Int, height: Int): ImageIcon =
        new ImageIcon(scaling.getImage.getScaledInstance(width, height, Image.SCALE_DEFAULT))

    def makeIconButton(button: JButton, icon: ImageIcon, width: Int, height: Int): Unit =
    {
        button.setIcon(getScaledImageIcon(icon, width, height))
        button.setSize(width, height)
        initIconButton(button)
    }

    def makeAdaptiveIconButton(button: JButton, path: String, max_size: Int): Unit =
    {
        try
        {
            val url = new URL(path)
            val img = new ImageIcon(url)
            val size = getAdaptiveScale(img, max_size, by_height = true)
            button.setIcon(getScaledImageIcon(img, size.width, size.height))
            button.setSize(size)
            initIconButton(button)
        }
        catch
        {
            case ex: MalformedURLException =>
                ex.printStackTrace()
        }
    }

    def getAdaptiveScale(image: ImageIcon, max_size: Int, by_height: Boolean): Dimension =
        if (by_height || image.getIconWidth < image.getIconHeight)
            new Dimension(((image.getIconWidth.toFloat / image.getIconHeight) * max_size).toInt, max_size)
        else
            new Dimension(max_size, ((image.getIconHeight.toFloat / image.getIconWidth) * max_size).toInt)

    def initIconButton(button: JButton): Unit =
    {
        button.setContentAreaFilled(false)
        button.setBorder(null)
        button.setCursor(new Cursor(Cursor.HAND_CURSOR))
    }
}
