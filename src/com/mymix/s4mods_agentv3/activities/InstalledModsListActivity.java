package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.models.Mod;

import javax.swing.*;
import java.awt.*;

public class InstalledModsListActivity extends ModsListActivity
{
    @Override
    public void init()
    {

    }

    @Override
    protected void loadFilters()
    {

    }

    // TODO: optimize displaying to installed mods
    public void addMod(Mod mod)
    {
        JPanel mod_panel = new JPanel(new GridBagLayout()),
                top_panel = new JPanel(new BorderLayout()),
                name_group = new JPanel(new FlowLayout()),
                control_group = new JPanel(new FlowLayout());
        JLabel name = new JLabel(mod.name()),
                installed = new JLabel();
        JButton add_remove = new JButton(),
                on_off = new JButton(),
                image = new JButton();
        JTextPane desc = new JTextPane();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.ipadx = 10;
        c.ipady = 5;
        mod_panel.add(image, c);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        name_group.add(name);
        name_group.add(installed);

        makeIconButton(add_remove, "res/icons/download-icon.png", 20, 20);
        control_group.add(add_remove);
        makeIconButton(on_off, "res/icons/enable-icon.png", 20, 20);
        control_group.add(on_off, c);

        top_panel.add(name_group, BorderLayout.WEST);
        top_panel.add(control_group, BorderLayout.EAST);
        mod_panel.add(top_panel, c);

        makeAdaptiveIconButton(image, "test-res/loaded.jpg", Math.max(desc.getSize(null).height, 155));

        c.gridx = 2;
        c.gridy = 1;
        desc.setEditable(false);
        desc.setText(mod.description());
        int w_max = Main.getWidth();
        w_max -= filters_panel.getMaximumSize().width + 100;
        w_max -= image.getIcon().getIconWidth();
        int w = desc.getText().length() * desc.getFont().getSize();
        Dimension s = new Dimension(Math.min(w, w_max), image.getIcon().getIconHeight());
        desc.setPreferredSize(s);
        JScrollPane desc_container = new JScrollPane(desc);
        desc_container.setPreferredSize(s);
        desc_container.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mod_panel.add(desc_container, c);

        mods.add(mod_panel, conf);
    }
}
