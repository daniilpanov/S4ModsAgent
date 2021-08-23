import java.awt._

import javax.swing._

object ModsListTest extends JFrame
{
    def main(args: Array[String]): Unit =
    {
        setTitle("Mods List Test!")
        setSize(1000, 700)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        val contentPane = new JPanel(new GridBagLayout)
        setLayout(new BorderLayout())
        add(contentPane, BorderLayout.CENTER)

        val c = new GridBagConstraints
        c.fill = GridBagConstraints.BOTH
        c.anchor = GridBagConstraints.CENTER

        c.gridx = 0
        c.gridy = 0
        c.gridwidth = 2
        c.gridheight = 2
        c.ipadx = 10
        c.ipady = 5
        val imagev2 = new JButton()
        contentPane.add(imagev2, c)

        c.gridx = GridBagConstraints.RELATIVE
        c.gridy = 0
        c.gridwidth = 1
        c.gridheight = 1
        val top_panel = new JPanel(new BorderLayout())
        val name_group = new JPanel(new FlowLayout())
        val name = new JLabel("НАЗВАНИЕ МОДА!!!")
        name_group.add(name)
        val installed = new JLabel("Не установлен")
        name_group.add(installed)
        top_panel.add(name_group, BorderLayout.WEST)

        val control_group = new JPanel(new FlowLayout())
        val add_remove = new JButton()
        makeIconButton(add_remove, "res/icons/download-icon.png", 20, 20)
        control_group.add(add_remove)

        val on_off = new JButton()
        makeIconButton(on_off, "res/icons/enable-icon.png", 20, 20)
        control_group.add(on_off)
        top_panel.add(control_group, BorderLayout.EAST)

        contentPane.add(top_panel, c)

        c.gridx = 2
        c.gridy = 1
        val desc = new JTextArea("Описание11111111111111111111111111111111111111111111111111111\nОписание\nОписание")
        desc.setWrapStyleWord(true)
        desc.setEditable(false)
        contentPane.add(desc, c)

        makeAdaptiveIconButton(imagev2, "test-res/loaded.jpg", Math.max(desc.getSize(null).height, 155))

        setVisible(true)
    }

    def makeIconButton(button: JButton, path: String, width: Int, height: Int): Unit =
    {
        button.setIcon(new ImageIcon(new ImageIcon(path).getImage
            .getScaledInstance(width, height, Image.SCALE_DEFAULT)))
        button.setSize(width, height)
        initIconButton(button)
    }

    def makeAdaptiveIconButton(button: JButton, path: String, max_size: Int): Unit =
    {
        val img = new ImageIcon(path)
        val size = getAdaptiveScale(img, max_size)
        button.setIcon(new ImageIcon(img.getImage
            .getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT)))
        button.setSize(size)
        initIconButton(button)
    }

    def getAdaptiveScale(image: ImageIcon, max_size: Int): Dimension =
        if (image.getIconWidth > image.getIconHeight)
            new Dimension(max_size, image.getIconHeight / image.getIconWidth * max_size)
        else
            new Dimension(image.getIconWidth / image.getIconHeight * max_size, max_size)

    def initIconButton(button: JButton): Unit =
    {
        button.setContentAreaFilled(false)
        button.setBorder(null)
        button.setCursor(new Cursor(Cursor.HAND_CURSOR))
    }
}
