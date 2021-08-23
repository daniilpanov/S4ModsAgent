package com.mymix.s4mods_agentv2.controllers;

import com.mymix.s4mods_agentv2.Constants;
import com.mymix.s4mods_agentv2.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

public class BackgroundTasks extends Thread
{
    public final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    public final List<Task> tasks_added = Collections.synchronizedList(new ArrayList<>());
    public final List<Integer> tasks_deleted = Collections.synchronizedList(new ArrayList<>());
    public boolean ok = true;


    public BackgroundTasks()
    {
        super("BackgroundTasksThread");
        setPriority(Thread.MIN_PRIORITY);

        if (ok)
            setRunning(true, true);
    }

    @Override
    public void run()
    {
        super.run();

        synchronized (this)
        {
            try
            {
                synchronized (tasks_deleted)
                {
                    tasks_deleted.forEach(e -> tasks.remove(e.intValue()));
                    tasks_deleted.clear();
                }
                synchronized (tasks_added)
                {
                    tasks.addAll(tasks_added);
                    tasks_added.clear();
                }
                tasks.forEach(Runnable::run);

            }
            catch (ConcurrentModificationException ex)
            {
                Constants.log("FUCK!!!");
            }
            finally
            {
                try
                {
                    this.wait(1000);
                }
                catch (InterruptedException ignored)
                {
                }
                finally
                {
                    if (ok)
                        run();
                }
            }
        }
    }

    public int addTask(Runnable r, boolean repeat)
    {
        synchronized (tasks_added)
        {
            Task task = new Task(r);
            task.setRepeating(repeat);

            tasks_added.add(task);

            setRunning(true, repeat);

            return task.ID;
        }
    }

    public void removeTask(int ID)
    {
        synchronized (tasks_deleted)
        {
            for (int i = 0; i < tasks.size(); ++ i)
            {
                if (tasks.get(i).ID == ID)
                {
                    tasks_deleted.add(i);
                    break;
                }
            }
        }
    }

    public void setRunning(boolean s, boolean r)
    {
        if (!r)
        {
            if  (!isAlive())
                ok = false;
        }
        else
            ok = true;

        if (s && !isAlive())
            start();
        else if (!s && !isInterrupted())
            interrupt();
    }
}
