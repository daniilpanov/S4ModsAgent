package com.mymix.s4mods_agentv2.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InstalledModsList
{
    // TODO: получение модов, активирование/деактивирование модов (перенос в/из папки Mods), добавление и удаление
    public File mods_dir;
    public File disabled_mods_dir;
    public final String path_to_EA = System.getProperty("user.home") + "\\Documents\\Electronic Arts\\";
    public String sims_dir = "The Sims 4";

    public final List<Mod> mods = new ArrayList<>();

    public InstalledModsList()
    {
        mods_dir = new File(path_to_EA + sims_dir + "\\Mods");
        disabled_mods_dir = new File(path_to_EA + sims_dir + "\\DisabledMods");
    }

    public List<Mod> getModsList()
    {
        if (mods.isEmpty())
        {

        }

        return mods;
    }

    public List<Mod> getModsOnly(boolean active)
    {
        return new ArrayList<>();
    }

    public void setModActive(Mod mod, boolean active)
    {

    }

    public void addMod(Mod mod)
    {

    }

    public void deleteMod(Mod mod)
    {

    }

    public boolean isModInstalled(Mod mod)
    {
        return false;
    }
}
