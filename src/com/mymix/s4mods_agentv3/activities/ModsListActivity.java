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

        contentPane.add(this);
    }

    abstract public void addMod(Mod mod);

    protected void makeIconButton(JButton button, String path, int width, int height)
    {
        button.setIcon(new ImageIcon(new ImageIcon(path).getImage()
                .getScaledInstance(width, height, Image.SCALE_DEFAULT)));
        button.setSize(width, height);
        initIconButton(button);
    }

    protected void makeAdaptiveIconButton(JButton button, String path, int max_size)
    {
        try
        {
            URL url = new URL(path);
            ImageIcon img = new ImageIcon(url);
            Dimension size = getAdaptiveScale(img, max_size, true);
            button.setIcon(new ImageIcon(img.getImage()
                    .getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT)));
            button.setSize(size);
            initIconButton(button);
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
    }

    protected Dimension getAdaptiveScale(ImageIcon image, int max_size, boolean by_height)
    {
        if (by_height || image.getIconWidth() < image.getIconHeight())
            return new Dimension((int) (((float) image.getIconWidth() / image.getIconHeight()) * max_size), max_size);
        else
            return new Dimension(max_size, (int) (((float) image.getIconHeight() / image.getIconWidth()) * max_size));
    }

    protected void initIconButton(JButton button)
    {
        button.setContentAreaFilled(false);
        button.setBorder(null);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
