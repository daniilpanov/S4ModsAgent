package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.UIDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartActivity extends Activity
{
    private JLabel text;
    private JPanel menu;

    @Override
    public void init()
    {
        setLayout(new BorderLayout());

        text = new JLabel();
        text.setBackground(new Color(0, 0, 0, 0));
        text.setForeground(new Color(0, 85, 255));
        text.setText("<html><body><center><h1>ДОБРО ПОЖАЛОВАТЬ<BR>В МИР СИМС!</h1></center></body></html>");
        add(UIDecorator.getCenteredComponent(text), BorderLayout.NORTH);

        menu = new JPanel(new GridLayout(3, 2, 3, 2));
        menu.setBackground(new Color(0, 0, 0, 0));
        add(UIDecorator.getCenteredComponent(menu), BorderLayout.CENTER);

        addButton("Все моды", e -> Main.activity(new OnlineModsListActivity()));
        addButton("Библиотека модов", e -> Main.activity(new InstalledModsListActivity()));
        addButton("Настройки", e -> Main.activity(new SettingsActivity()));
        addButton("Темы", e -> Main.activity(new ThemesActivity()));
        addButton("Загрузки", e -> Main.activity(new DownloadingActivity()));
        addButton("Галерея", e -> Main.activity(new GalleryActivity()));
    }

    private void addButton(String text, ActionListener click_ev)
    {
        JButton nb = new JButton(text);
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

        Constants.log("ok");

        ImageIcon bg_instance =
                new ImageIcon("res/grand-theft-auto-v-internet-zakat-panorama-oboi-4320x960_157.jpg");
        Dimension bg_scaled_size = UIDecorator.getAdaptiveScale(
                bg_instance,
                700, true
        );
        ImageIcon bg = UIDecorator.getScaledImageIcon(bg_instance, bg_scaled_size.width, bg_scaled_size.height);

        g.drawImage(bg.getImage(), -1000, 0, null);
    }
}
