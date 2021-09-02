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
    // за единицу берем ширину панели
    private double current_slide_pos = 0;

    public static int animation_speed = 10;
    public Timer animation_ticks = null;

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
	g.drawImage(images.get(current).getImage(), current_slide_pos * getWidth(), 0, null);
    }

    private void startShifts(int direction)
    {
	animation = direction;
	if (animation_ticks != null)
		stopShifts();
	animation_ticks = new Timer(() ->
	{
	    slideShiftIteration();
	}, 100);
	animation_timer.start();
    }

    private void slideShiftIteration()
    {
	current_slide_pos += animation * (animation_speed / getWidth());
	if (Math.abs(current_slide_pos) >= Math.abs(animation))
	    stopShifts();
	repaint();
    }

    private void stopShifts()
    {
	animation = 0;
	current_slide_pos = 0;
	if (animation_ticks != null)
	{
	    animation_ticks.stop();
	    animation_ticks = null;
	}
    }
}
