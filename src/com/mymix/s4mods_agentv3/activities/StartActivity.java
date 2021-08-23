package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartActivity extends Activity
{
    private JTextPane text;
    private JPanel left_menu;
    private GroupLayout.Group vg, hg;

    @Override
    public void init()
    {
        setLayout(new BorderLayout());

        text = new JTextPane();
        text.setContentType("text/html");
        text.setEditable(false);
        text.setText("<html><body><center><h2>Приветствуем!</h2></center></body></html>");
        add(text, BorderLayout.CENTER);

        left_menu = new JPanel();
        GroupLayout ml = new GroupLayout(left_menu);
        left_menu.setLayout(ml);
        add(left_menu, BorderLayout.WEST);

        vg = ml.createSequentialGroup();
        hg = ml.createParallelGroup();
        ml.setVerticalGroup(vg);
        ml.setHorizontalGroup(hg);

        addButton("Все моды", e -> Main.activity(new OnlineModsListActivity()));
        addButton("Выход", e -> System.exit(0));
    }

    private void addButton(String text, ActionListener click_ev)
    {
        JButton nb = new JButton(text);
        vg.addComponent(nb);
        hg.addComponent(nb);
        nb.addActionListener(click_ev);
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
    }
}
