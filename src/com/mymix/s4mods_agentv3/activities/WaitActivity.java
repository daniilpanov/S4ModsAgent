package com.mymix.s4mods_agentv3.activities;

import javax.swing.*;
import java.awt.*;

public class WaitActivity extends Activity
{
    @Override
    public int init()
    {
        setLayout(new FlowLayout());
        add(new JLabel("Please, wait..."));
        return 0;
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
    }
}
