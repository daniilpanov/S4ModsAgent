import com.mymix.s4mods_agentv3.udd.Properties;
import com.mymix.s4mods_agentv3.udd.UDD;
import com.mymix.s4mods_agentv3.udd.ViewObject;
import com.mymix.s4mods_agentv3.udd.aClass;

import javax.swing.*;
import java.awt.*;

public class TestUDD
{
    public static void main(String[] args)
    {
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(90, 90, 1000, 800);
        f.setVisible(true);
        f.setLayout(new BorderLayout());

        JButton btn = new JButton("test");
        //btn.setMargin(new Insets(100, 100, 100, 100));
        ViewObject<JButton> nbtn = new ViewObject<>(btn);
        Properties props = new Properties();
        props.set("background", Integer.toString(UDD.getColorCode(Color.RED)));
        props.set("color", Integer.toString(UDD.getColorCode(Color.GREEN)));
        nbtn.addClass(new aClass("first", props));

        f.add(btn, BorderLayout.CENTER);
    }
}
