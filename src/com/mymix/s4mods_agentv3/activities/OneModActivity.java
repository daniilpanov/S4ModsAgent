package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.models.Mod;

import java.awt.*;

public class OneModActivity extends Activity
{
    public Mod mod;


    public OneModActivity(Mod mod)
    {
        this.mod = mod;
    }

    @Override
    public int init()
    {
        return 0;
    }

    @Override
    public void setActive(Container contentPane)
    {
        contentPane.add(this);
    }
}
