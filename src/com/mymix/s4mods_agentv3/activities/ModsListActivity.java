package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.UIDecorator;
import com.mymix.s4mods_agentv3.models.Mod;

import javax.swing.*;
import java.awt.*;

abstract public class ModsListActivity extends Activity
{
    protected JPanel top_menu = new JPanel(new FlowLayout(FlowLayout.LEFT)),
            filters_panel = new JPanel(),
            mods = new JPanel();
    protected JScrollPane filters_scroll = new JScrollPane(filters_panel);
    protected JScrollPane mods_scroll = new JScrollPane(mods);
    protected JPanel pagination = new JPanel(new FlowLayout());
    protected GridBagConstraints conf = new GridBagConstraints();


    public ModsListActivity()
    {
        super();
        filters_panel.setLayout(new BoxLayout(filters_panel, BoxLayout.Y_AXIS));
        UIDecorator.setComponentTransparent(filters_panel);
        filters_scroll.getVerticalScrollBar().addAdjustmentListener(e -> this.repaint());

        mods.setLayout(new GridBagLayout());
        mods_scroll.getVerticalScrollBar().addAdjustmentListener(e -> this.repaint());
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
        UIDecorator.setComponentTransparent(filters_panel);
        UIDecorator.setComponentTransparent(filters_scroll);
        filters_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        filters_scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(filters_scroll, BorderLayout.WEST);
        // mods
        UIDecorator.setComponentTransparent(mods);
        UIDecorator.setComponentTransparent(mods_scroll);
        mods_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mods_scroll.setWheelScrollingEnabled(true);
        mods_scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mods_scroll, BorderLayout.CENTER);
        mods_scroll.getVerticalScrollBar().setValue(mods_scroll.getVerticalScrollBar().getMinimum());
        // pagination
        UIDecorator.setComponentTransparent(pagination);
        add(pagination, BorderLayout.SOUTH);

        contentPane.add(this);
    }

    abstract public void addMod(Mod mod);

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
}
