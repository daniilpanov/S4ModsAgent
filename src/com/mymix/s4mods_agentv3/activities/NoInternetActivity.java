package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Main;

import javax.swing.*;
import java.awt.*;

public class NoInternetActivity extends Activity
{
    private Activity activity;

    public NoInternetActivity(Activity activity)
    {
        this.activity = activity;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public int init()
    {
        JLabel no_internet = new JLabel("Нет подключения к интернету!");
        no_internet.setFont(new Font("Verdana", Font.BOLD, 28));
        no_internet.setForeground(Color.red);
        add(no_internet);

        JButton reload = new JButton("Перезагрузить страницу");
        reload.addActionListener(l ->
        {
            Main.activity(activity);
        });
        add(reload);

        return 0;
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
    }
}
