package com.mymix.s4mods_agentv3.components;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModDescScrollPane extends JScrollPane
{
    public boolean focus = false;
    private JScrollPane parent;

    public ModDescScrollPane(Component c, JScrollPane parent)
    {
        super(c);
        this.parent = parent;

        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        getVerticalScrollBar().setUnitIncrement(5);

        addMouseWheelListener(l ->
        {
            Constants.log(focus);
            if (!focus)
            {
                parent.getVerticalScrollBar().setValue(
                        parent.getVerticalScrollBar().getValue() +
                                l.getUnitsToScroll()
                                        * parent.getVerticalScrollBar()
                                        .getUnitIncrement());
                int val = verticalScrollBar.getValue() - l.getUnitsToScroll()
                                * verticalScrollBar.getUnitIncrement();
                if (val < verticalScrollBar.getMinimum())
                    verticalScrollBar.setValue(verticalScrollBar.getMinimum());
                else
                    verticalScrollBar.setValue(val);
            }
        });

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseExited(MouseEvent e)
            {
                super.mouseExited(e);
                focus = false;
                Constants.log("exited");
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                super.mouseEntered(e);
                focus = false;
                Constants.log("entered");
                Constants.log(focus);
            }
        });

        AWTEventListener listener = event ->
        {
            if (MouseEvent.MOUSE_CLICKED == event.getID())
            {
                MouseEvent e = (MouseEvent) event;
                Component comp = e.getComponent();

                if (comp != null)
                {
                    ModDescScrollPane scroll = recursivelyCheckForScrollPanel(comp);
                    focus = scroll != null && scroll.getVerticalScrollBar().isVisible();
                    Constants.log("clicked");
                }
            }
        };
        Toolkit.getDefaultToolkit()
                .addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private ModDescScrollPane recursivelyCheckForScrollPanel(Component comp)
    {
        if (comp instanceof ModDescScrollPane)
        {
            return (ModDescScrollPane) comp;
        }
        else
        {
            comp = comp.getParent();

            if (comp != null)
            {
                return recursivelyCheckForScrollPanel(comp);
            }
        }

        return null;
    }
}
