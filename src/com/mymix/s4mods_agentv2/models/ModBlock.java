package com.mymix.s4mods_agentv2.models;

import com.mymix.s4mods_agentv2.Constants;
import com.mymix.s4mods_agentv2.controllers.InstallMod;
import com.mymix.s4mods_agentv2.models.Mod;

import javax.swing.*;

public class ModBlock extends JPanel
{
    public Mod mod;
    public boolean displayed = false;

    public ModBlock(Mod mod)
    {
        this.mod = mod;

        JLabel name = new JLabel(mod.name());
        JLabel desc = new JLabel(mod.description());
        JButton download = new JButton("Установить");

        download.addActionListener(e -> new InstallMod(Constants.URL() + mod.download_link()));

        /// Layout
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // HorizontalGroup
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(name)
                        .addComponent(desc)
                        .addComponent(download)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(name)
                        .addComponent(desc)
                        .addComponent(download)
        );
    }


}
