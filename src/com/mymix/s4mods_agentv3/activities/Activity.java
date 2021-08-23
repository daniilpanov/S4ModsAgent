package com.mymix.s4mods_agentv3.activities;

import javax.swing.*;
import java.awt.*;

abstract public class Activity extends JPanel
{
    abstract public void init();
    abstract public void setActive(Container contentPane);
    public void setInactive(Container contentPane)
    {
        contentPane.remove(this);
    }
}
