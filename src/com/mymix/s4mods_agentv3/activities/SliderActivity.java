package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.UIDecorator;
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
    private double current_slide_pos;
    private double stop_point = 0;

    public static int animation_speed = 50;
    public Timer animation_ticks = null;

    private JButton next = new JButton(">"), pre = new JButton("<");

    private JDialog slider_window = null;


    public SliderActivity(String mod_link)
    {
        setSize(800, 600);

        List<String> links = ModsOnlineController.getSliderImagesLink(mod_link);
        links.forEach(link ->
        {
            try
            {
                ImageIcon img = new ImageIcon(new URL(link));
                if (img.getIconWidth() > getWidth())
                {
                    Dimension img_size = UIDecorator.getAdaptiveScale(img, 600, false);
                    img = new ImageIcon(img.getImage()
                            .getScaledInstance(img_size.width, img_size.height, Image.SCALE_DEFAULT));
                }
                else if (img.getIconWidth() > getWidth())
                {
                    Dimension img_size = UIDecorator.getAdaptiveScale(img, 800, false);
                    img = new ImageIcon(img.getImage()
                            .getScaledInstance(img_size.width, img_size.height, Image.SCALE_DEFAULT));
                }
                images.add(img);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        });

        setLayout(new BorderLayout());

        if (images.size() > 1)
        {
            add(pre, BorderLayout.WEST);
            add(next, BorderLayout.EAST);

            pre.addActionListener(l ->
            {
                startShifts((short) 1);
            });

            next.addActionListener(l ->
            {
                startShifts((short) -1);
            });
        }

        current_slide_pos = getCenterPos();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);


        drawImage(g, current, (int) (current_slide_pos * getWidth()));

        if (animate == 1)
        {
            drawImage(g, getNextSlide(), 0 - ((int) ((1 - current_slide_pos) * getWidth())));
        }
        else if (animate == -1)
        {
            drawImage(g, getNextSlide(), getWidth() + ((int) (current_slide_pos * getWidth())));
        }
    }

    private void drawImage(Graphics g, int img_index, int x)
    {
        ImageIcon img = images.get(img_index);
        g.drawImage(img.getImage(), x, (getHeight() - img.getIconHeight()) / 2, null);
    }

    private void startShifts(short direction)
    {
        if (animation_ticks != null)
            stopShifts();
        animate = direction;
        animation_ticks = new Timer(25, l -> slideShiftIteration());
        stop_point = getNextCenterPos();
        animation_ticks.start();
    }

    private void slideShiftIteration()
    {
        current_slide_pos += animate * ((float) animation_speed / getWidth());
        if (animate == -1 && getWidth() + ((int) (current_slide_pos * getWidth())) <= stop_point * getWidth())
            stopShifts();
        else if (0 - ((int) ((1 - current_slide_pos) * getWidth())) >= stop_point * getWidth())
            stopShifts();
        repaint();
    }

    private void stopShifts()
    {
        current = getNextSlide();
        animate = 0;
        current_slide_pos = getCenterPos();
        if (animation_ticks != null)
        {
            animation_ticks.stop();
            animation_ticks = null;
        }
    }

    private int getNextSlide()
    {
        if (current + animate < 0)
            return images.size() - 1;
        else if (current + animate >= images.size())
            return 0;
        else
            return current + animate;
    }

    private double getCenterPos()
    {
        return (double) (getWidth() - images.get(current).getIconWidth()) / (2 * getWidth());
    }

    private double getNextCenterPos()
    {
        return (double) (getWidth() - images.get(getNextSlide()).getIconWidth()) / (2 * getWidth());
    }

    @Override
    public int init()
    {
        return 0;
    }

    @Override
    public void setActive(Container parent_window)
    {
        slider_window = new JDialog((JFrame) parent_window, "", true);
        slider_window.setSize(800, 600);
        UIDecorator.setCenteredWindow(slider_window);
        slider_window.add(this);
        slider_window.setVisible(true);
    }
}
