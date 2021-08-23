package com.mymix.s4mods_agentv2.actions;

import com.mymix.s4mods_agentv2.Main;

import javax.swing.*;
import java.awt.*;

public class StartAction extends Action
{
    private JLabel content;
    private JButton search = new JButton("Искать моды!"),
            list_installed = new JButton("Список установленных модов");

    public StartAction()
    {
        // TODO: make this page using BoxLayout
        content = new JLabel("<html><head><style></style></head>" +
                "<body><h1>Менеджер Модов Sims 4</h1></body></html>");
        content.setBackground(Color.CYAN);

        add(content);
        add(search);
        add(list_installed);
    }

    @Override
    public void init()
    {
        search.addActionListener(e -> Main.searchAction(null, null));
        list_installed.addActionListener(e -> Main.installedManagerAction());
    }
}
