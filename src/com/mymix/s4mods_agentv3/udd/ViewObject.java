package com.mymix.s4mods_agentv3.udd;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewObject <T extends Component>
{
    private List<aClass> classes = new ArrayList<>();
    private Properties private_props = null;

    private Component base_comp;

    public ViewObject(Component c)
    {
        base_comp = c;
    }

    public T baseObject()
    {
        return (T) base_comp;
    }

    public void getClass(String classname)
    {

    }

    public void addClass(String classname)
    {

    }

    public void addClass(aClass new_class)
    {

    }

    public void deleteClass(String classname)
    {

    }


    public Properties getPrivateProps()
    {
        return private_props;
    }

    public void setPrivateProps(Properties props)
    {
        private_props = props;
    }

    /// MAIN PART
    public void update()
    {

    }
}
