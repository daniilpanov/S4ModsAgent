package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.RoundedCornerBorder;
import com.mymix.s4mods_agentv3.UIDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StartActivity extends Activity
{
    private JLabel text;
    private JPanel menu;
    private JLabel noInternet = new JLabel("     ");
    Component noInternetCentered = UIDecorator.getCenteredComponent(noInternet);
    private Timer inetConn = new Timer(10000, e ->
    {
        Main.checkInternetConnection();
        if (Main.internet_connection())
        {
            noInternet.setText("      ");
            UIDecorator.setComponentTransparent(noInternetCentered);
            repaint();
        }
        else
        {
            noInternet.setText("No internet connection!");
            noInternetCentered.setBackground(Color.RED);
            repaint();
        }
    });

    @Override
    public void init()
    {
        setLayout(new BorderLayout());
        inetConn.start();

        text = new JLabel();
        text.setBackground(new Color(0, 0, 0, 0));
        text.setForeground(new Color(0, 85, 255));
        text.setText("<html><body><center><h1>ДОБРО ПОЖАЛОВАТЬ<BR>В МИР СИМС!</h1></center></body></html>");
        add(UIDecorator.getCenteredComponent(text), BorderLayout.NORTH);

        menu = new JPanel(new GridLayout(3, 2, 3, 2));
        menu.setBackground(new Color(0, 0, 0, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));

        addButton("Все моды", e -> Main.activity(new OnlineModsListActivity()));
        addButton("Библиотека модов", e -> Main.activity(new InstalledModsListActivity()));
        addButton("Настройки", e -> Main.activity(new SettingsActivity()));
        addButton("Темы", e -> Main.activity(new ThemesActivity()));
        addButton("Загрузки", e -> Main.activity(new DownloadingActivity()));
        addButton("Галерея", e -> Main.activity(new GalleryActivity()));

        add(UIDecorator.getCenteredComponent(menu), BorderLayout.CENTER);

        noInternet.setForeground(new Color(100, 0, 0));
        noInternetCentered.setPreferredSize(
                new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50)
        );
        add(noInternetCentered, BorderLayout.NORTH);
        noInternet.setPreferredSize(new Dimension(145, 40));
    }

    private void addButton(String text, ActionListener click_ev)
    {
        JButton nb = new JButton(text)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                if (!isOpaque() && getBorder() instanceof RoundedCornerBorder)
                {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(getBackground());
                    g2.fill(((RoundedCornerBorder) getBorder()).getBorderShape(
                            0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }

                g.setColor(new Color(0, 46, 255));
                g.drawChars(getText().toCharArray(), 0, getText().length(),
                        getHorizontalTextPosition() + 6, getVerticalTextPosition() + 26);

                super.paintComponent(g);
            }

            @Override
            public void updateUI()
            {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedCornerBorder(new Color(0, 46, 255), 5));
            }
        };
        nb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nb.setContentAreaFilled(false);
        nb.setBackground(new Color(0, 0, 0, 0));
        nb.setForeground(new Color(123, 255, 0));
        nb.setFocusable(false);
        nb.setHorizontalAlignment(JButton.LEFT);
        nb.setVerticalAlignment(JButton.CENTER);
        nb.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mouseClicked(e);
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                super.mouseEntered(e);
                nb.setBorder(new RoundedCornerBorder(new Color(123, 255, 0), 5));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                super.mouseExited(e);
                nb.setBorder(new RoundedCornerBorder(new Color(0, 46, 255), 5));
                repaint();
            }
        });
        Font f = new Font("Verdana", Font.ITALIC, 16);
        nb.setFont(f);

        menu.add(nb);
        nb.addActionListener(click_ev);
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        ImageIcon bg_instance = new ImageIcon(
                "res/grand-theft-auto-v-internet-zakat-panorama-oboi-4320x960_157.jpg");
        Dimension bg_scaled_size = UIDecorator.getAdaptiveScale(bg_instance, Main.getHeight(), true);
        ImageIcon bg = UIDecorator.getScaledImageIcon(bg_instance, bg_scaled_size.width, bg_scaled_size.height);

        g.drawImage(bg.getImage(), -1000, 0, null);
    }

    @Override
    public void setInactive(Container contentPane)
    {
        super.setInactive(contentPane);
        inetConn.stop();
    }
}
