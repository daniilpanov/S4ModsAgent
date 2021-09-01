package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.models.Mod;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

abstract public class ModsListActivity extends Activity
{
    protected JPanel top_menu = new JPanel(new FlowLayout(FlowLayout.LEFT)),
            filters_panel = new JPanel();
    protected JScrollPane mods_scroll;
    protected JPanel mods = new JPanel();
    protected JPanel pagination = new JPanel(new FlowLayout());
    protected GridBagConstraints conf = new GridBagConstraints();


    public ModsListActivity()
    {
        super();
        filters_panel.setLayout(new BoxLayout(filters_panel, BoxLayout.Y_AXIS));

        mods.setLayout(new GridBagLayout());
        conf.fill = GridBagConstraints.BOTH;
        conf.anchor = GridBagConstraints.BASELINE;

        conf.gridx = 1;
        conf.gridy = GridBagConstraints.RELATIVE;
        conf.weightx = 1;
        conf.weighty = 1;
        conf.ipadx = 10;
        conf.ipady = 25;
    }

    abstract protected void loadFilters();

    @Override
    public void setActive(Container contentPane)
    {
        setLayout(new BorderLayout());
        // menu
        add(top_menu, BorderLayout.NORTH);
        // filters
        filters_panel.setMaximumSize(new Dimension(300, 2000));
        JScrollPane filters_scroll = new JScrollPane(filters_panel);
        filters_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        filters_scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(filters_scroll, BorderLayout.WEST);
        // mods
        mods_scroll = new JScrollPane(mods);
        mods_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mods_scroll.setWheelScrollingEnabled(true);
        mods_scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mods_scroll, BorderLayout.CENTER);
        mods_scroll.getVerticalScrollBar().setValue(mods_scroll.getVerticalScrollBar().getMinimum());
        // pagination
        add(pagination, BorderLayout.SOUTH);

        contentPane.add(this);
    }

    abstract public void addMod(Mod mod);
}
