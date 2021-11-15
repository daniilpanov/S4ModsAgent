package com.mymix.s4mods_agentv3.udd;

public class aClass
{
    String name;
    Properties props;

    public aClass(String name)
    {
        this.name = name;
    }

    public aClass(String name, Properties props)
    {
        this(name);
        this.props = props;
    }

    public String getName()
    {
        return name;
    }

    public Properties getProps()
    {
        return props;
    }
}
