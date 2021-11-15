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

    public List<aClass> getClasses()
    {
        return classes;
    }

    public aClass getClass(String classname)
    {
        aClass res = null;

        for (aClass cl : classes)
        {
            if (classname.equals(cl.getName()))
                res = cl;
        }

        return res;
    }

    public void addClass(String classname)
    {
        aClass cl = UDD.getClass(classname);

        if (cl != null)
            classes.add(cl);

        update();
    }

    public void addClass(aClass new_class)
    {
        if (UDD.addClass(new_class))
            classes.add(new_class);

        update();
    }

    public void deleteClass(String classname)
    {
        for (int i = 0; i < classes.size(); ++i)
        {
            if (classname.equals(classes.get(i).getName()))
                classes.remove(i);
        }
        // TODO: in method 'update' the deleted properties must also delete
        update();
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
        Properties props;
        String val;

        for (aClass cl : classes)
        {
            props = cl.getProps();

            if ((val = props.get("background")) != null)
            {
                base_comp.setBackground(new Color(Integer.parseInt(val)));
            }
            if ((val = props.get("color")) != null)
            {
                base_comp.setForeground(new Color(Integer.parseInt(val)));
            }
        }
    }
}
