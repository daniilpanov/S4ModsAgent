package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.controllers.ModsOnlineController;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SliderActivity extends Activity
{
    private List<ImageIcon> images = new ArrayList<>();
    private int current = 0;
    private short animate = 0;

    public SliderActivity(String mod_link)
    {
        List<String> links = ModsOnlineController.getSliderImagesLink(mod_link);
        links.forEach(link ->
        {
            try
            {
                ImageIcon img = new ImageIcon(new URL(link));
                images.add(img);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init()
    {
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
        contentPane.setComponentZOrder(this, 0);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(images.get(current).getImage(), 0, 0, null);
    }
}
