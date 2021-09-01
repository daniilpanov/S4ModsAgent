package com.mymix.s4mods_agentv3.activities;

import javax.swing.*;
import java.awt.*;

public class NoInternetActivity extends Activity
{
    @Override
    public void init()
    {
        JLabel no_internet = new JLabel("Нет подключения к интернету!");
        no_internet.setFont(new Font("Verdana", Font.BOLD, 28));
        no_internet.setForeground(Color.red);
        add(no_internet);
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
    }
}
