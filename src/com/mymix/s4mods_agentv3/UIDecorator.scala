package com.mymix.s4mods_agentv3

import java.awt._
import java.awt.event.{ActionListener, MouseAdapter, MouseEvent}
import java.awt.image.BufferedImage
import java.net.{MalformedURLException, URL}

import javax.swing._

object UIDecorator
{
    val transparent = new Color(0, 0, 0, 0)

    def getScaledImageIcon(scaling: ImageIcon, width: Int, height: Int): ImageIcon =
    {
        if (width == scaling.getIconWidth && height == scaling.getIconHeight)
            return scaling
        new ImageIcon(scaling.getImage.getScaledInstance(width, height, Image.SCALE_DEFAULT))
    }

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
            button.setIcon(new ImageIcon(
                cropImageIfLarge(
                    getScaledImageIcon(img, size.width, size.height).getImage,
                    200, 155)
                )
            )
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
        {
            val k = Math.min(max_size, image.getIconHeight)
            new Dimension(((image.getIconWidth.toFloat / image.getIconHeight) * k).toInt, k)
        }
        else
            new Dimension(max_size, ((image.getIconHeight.toFloat / image.getIconWidth) * max_size).toInt)

    def initIconButton(button: JButton): Unit =
    {
        button.setContentAreaFilled(false)
        button.setBorder(null)
        button.setCursor(new Cursor(Cursor.HAND_CURSOR))
    }

    def setCenteredWindow(window: Window): Unit =
    {
        val screen_size = Toolkit.getDefaultToolkit.getScreenSize
        val window_size = window.getSize
        window.setLocation((screen_size.width - window_size.width) / 2, (screen_size.height - window_size.height) / 2)
    }

    def createPrettyButton(name: String, l: ActionListener): JButton =
    {
        val item = new JButton(name)
        item.setContentAreaFilled(false)
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15))
        item.setCursor(new Cursor(Cursor.HAND_CURSOR))
        item.addActionListener(l)
        //normalizeElementRepaint(item)
        item
    }

    def cropImageIfLarge(img: Image, max_width: Int, max_height: Int): Image =
    {
        var x = 0
        var y = 0
        var width: Int = img.getWidth(null)
        var height: Int = img.getHeight(null)

        if (max_width < width)
        {
            x = -(width - max_width) / 2
            width = max_width
        }

        if (max_height < height)
        {
            y = -(height - max_height) / 2
            height = max_height
        }

        if (width < 0 || height < 0)
            return null
        val bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val bf_g: Graphics2D = bf.createGraphics()
        bf_g.drawImage(img, x, y, null)
        bf_g.dispose()

        bf
    }

    def getCenteredComponent(component: Component): Component =
    {
        val container = new JPanel(new FlowLayout())
        container add component
        container.setBackground(new Color(0, 0, 0, 0))
        container
    }

    def setComponentTransparent(component: Component): Unit =
    {
        /*component.setBackground(transparent)*/
    }

    def normalizeElementRepaint(component: Component, container: Container = null): Component =
    {
        /*val c = if (null == container)
            component.getParent else container

        component.addMouseListener(new MouseAdapter()
        {
            override def mousePressed(e: MouseEvent): Unit =
            {
                super.mouseClicked(e)
                c.repaint()
            }

            override

            def mouseEntered(e: MouseEvent): Unit =
            {
                super.mouseEntered(e)
                c.repaint()
            }

            override

            def mouseExited(e: MouseEvent): Unit =
            {
                super.mouseExited(e)
                c.repaint()
            }
        })*/

        component
    }
}
