package com.mymix.s4mods_agentv2.models;

import com.mymix.s4mods_agentv2.Main;

public class Task implements Runnable
{
    public static int curr_ID = 0;

    public int ID;
    public Runnable r;
    public boolean repeating = false;
    public boolean run = true;

    public Task(Runnable r)
    {
        this.ID = ++ curr_ID;
        this.r = r;
    }

    public void setRepeating(boolean repeating)
    {
        this.repeating = repeating;
    }

    @Override
    public void run()
    {
        if (run)
            r.run();

        if (!repeating)
        {
            run = false;
            Main.bt().removeTask(ID);
        }
    }
}
