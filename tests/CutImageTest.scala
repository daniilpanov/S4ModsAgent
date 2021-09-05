import java.awt.{Graphics, Graphics2D}
import java.awt.image.BufferedImage

import javax.swing.{ImageIcon, JFrame, JPanel}

object CutImageTest extends JPanel
{
    def main(args: Array[String]): Unit =
    {
        Frame.init(this)
    }

    object Frame extends JFrame
    {
        def init(panel: JPanel): Unit =
        {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
            setBounds(250, 100, 800, 600)
            getContentPane add panel
            setVisible(true)
        }
    }

    override def paintComponent(g: Graphics): Unit =
    {
        super.paintComponent(g)

        val img = new ImageIcon("test-res/loaded.jpg")
        val bf = new BufferedImage(
            Math.min(200, img.getIconWidth),
            Math.min(155, img.getIconHeight),
            BufferedImage.TYPE_INT_ARGB
        )
        val bf_g: Graphics2D = bf.createGraphics()
        bf_g.drawImage(img.getImage, 0, 0, null)
        bf_g.dispose()

        g.drawImage(bf, 0, 0, null)
    }
}
