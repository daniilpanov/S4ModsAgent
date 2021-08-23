package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.controllers.ModsController;
import com.mymix.s4mods_agentv3.controllers.ModsInstalledController;
import com.mymix.s4mods_agentv3.controllers.ModsOnlineController;
import com.mymix.s4mods_agentv3.models.CategoriesCollection;
import com.mymix.s4mods_agentv3.models.Mod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OnlineModsListActivity extends ModsListActivity
{
    private static JLabel category_name;
    private JPanel pagination = new JPanel(new FlowLayout());

    public OnlineModsListActivity()
    {
        super();
        ModsController.path_$eq(null);
    }

    public OnlineModsListActivity(String path)
    {
        super();
        ModsController.path_$eq(path);
    }

    @Override
    public void init()
    {
        category_name = new JLabel("Все моды");

        loadFilters();
        loadPagination();
        List<Mod> list = ModsController.getOnlineModsList();
        list.forEach(this::addMod);

        top_menu.setLayout(new GridLayout(2, 1));

        JPanel top_panel = new JPanel(new BorderLayout());
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menu.add(createPrettyButton("ГЛАВНАЯ", l -> Main.activity(new StartActivity())));
        top_panel.add(menu, BorderLayout.CENTER);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String search_str = (ModsController.path() != null && ModsController.path().matches("^/search\\?q="))
                ? ModsController.path().replace("/search?q=", "") : "";
        JTextField search_input = new JTextField(search_str);
        search_input.setColumns(25);
        search_input.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton search_go = new JButton("Поиск");
        search_go.addActionListener(l ->
        {
            Main.activity(new OnlineModsListActivity("/search?q=" + search_input.getText()));
        });
        search.add(search_input);
        search.add(search_go);
        top_panel.add(search, BorderLayout.EAST);

        top_menu.add(top_panel);

        JPanel filters = new JPanel(new FlowLayout());
        filters.add(category_name);
        top_menu.add(filters);
    }

    private void loadPagination()
    {
        int pm = ModsOnlineController.getPagination();
        List<JButton> pag = new ArrayList<>();
        JButton pre = new JButton("Назад");
        pre.addActionListener(l ->
        {

        });
        pagination.add(pre);

        if (ModsOnlineController.pagination_current() > 1)
        {

        }
    }

    protected void loadFilters()
    {
        CategoriesCollection list = ModsController.getFilters();

        addFilter("Все моды", null);
        list.forEach(e -> addFilter(e.name(), e.link()));
    }

    private void addFilter(String name, String link)
    {
        JButton b = createPrettyButton(name, l ->
        {
            Main.activity(new OnlineModsListActivity(link));
            category_name.setText(name);
        });

        if (category_name.getText().equals(name))
        {
            b.setBackground(Color.ORANGE);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.ORANGE, 3, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
        filters_panel.add(b);
    }

    private JButton createPrettyButton(String name, ActionListener l)
    {
        JButton item = new JButton(name);
        item.setContentAreaFilled(false);
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.addActionListener(l);

        return item;
    }

    public void addMod(Mod mod)
    {
        ModsInstalledController.isModInstalled(mod);
        JPanel mod_panel = new JPanel(new GridBagLayout()),
                top_panel = new JPanel(new BorderLayout()),
                name_group = new JPanel(new FlowLayout()),
                control_group = new JPanel(new FlowLayout());
        JLabel installed = new JLabel("(установлен)");
        JButton name = new JButton(mod.name()),
                download = new JButton(),
                remove = new JButton(),
                on = new JButton(),
                off = new JButton(),
                image = new JButton();
        JTextPane desc = new JTextPane();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.ipadx = 10;
        c.ipady = 5;
        mod_panel.add(image, c);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        name.addActionListener(l -> Main.activity(new OneModActivity(mod)));
        name.setContentAreaFilled(false);
        name_group.add(installed);
        name_group.add(name);

        makeIconButton(download, "res/icons/download-icon.png", 20, 20);
        makeIconButton(remove, "res/icons/delete-icon.png", 20, 20);
        download.addActionListener(l ->
        {
            Main.install(mod);
            installed.setVisible(true);
            remove.setVisible(true);
            download.setVisible(false);

            off.setVisible(true);
            on.setVisible(false);
        });
        remove.addActionListener(l ->
        {
            Main.delete(mod);
            download.setVisible(true);
            installed.setVisible(false);
            remove.setVisible(false);

            on.setVisible(false);
            off.setVisible(false);
        });
        control_group.add(download);
        control_group.add(remove);

        if (mod.installed() && !"".equals(mod.filename()))
        {
            installed.setVisible(true);
            remove.setVisible(true);
            download.setVisible(false);
        }
        else
        {
            download.setVisible(true);
            installed.setVisible(false);
            remove.setVisible(false);
        }

        makeIconButton(on, "res/icons/enable-icon.png", 20, 20);
        makeIconButton(off, "res/icons/disable-icon.png", 20, 20);
        on.addActionListener(l ->
        {
            ModsInstalledController.switchDisable(mod);
            off.setVisible(true);
            on.setVisible(false);
        });
        off.addActionListener(l ->
        {
            ModsInstalledController.switchDisable(mod);
            off.setVisible(false);
            on.setVisible(true);
        });
        control_group.add(on, c);
        control_group.add(off, c);

        if (!mod.installed() || !"".equals(mod.filename()))
        {
            on.setVisible(false);
            off.setVisible(false);
        }
        else if (mod.disabled())
        {
            on.setVisible(true);
            off.setVisible(false);
        }
        else
        {
            off.setVisible(true);
            on.setVisible(false);
        }

        top_panel.add(name_group, BorderLayout.WEST);
        top_panel.add(control_group, BorderLayout.EAST);
        mod_panel.add(top_panel, c);

        makeAdaptiveIconButton(image, mod.image(), Math.max(desc.getSize(null).height, 155));

        c.gridx = 2;
        c.gridy = 1;
        desc.setEditable(false);
        desc.setText(mod.description());
        int w_max = Main.getWidth();
        w_max -= filters_panel.getMaximumSize().width + 100;
        w_max -= image.getIcon().getIconWidth();
        int w = desc.getText().length() * desc.getFont().getSize();
        Dimension s = new Dimension(Math.min(w, w_max), image.getIcon().getIconHeight());
        desc.setPreferredSize(s);
        //JScrollPane desc_container = new JScrollPane(desc);
        //desc_container.setPreferredSize(s);
        //desc_container.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //mod_panel.add(desc_container, c);
        mod_panel.add(desc, c);

        mods.add(mod_panel, conf);
    }
}
