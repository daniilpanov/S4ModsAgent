package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Images;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.UIDecorator;
import com.mymix.s4mods_agentv3.controllers.ModsController;
import com.mymix.s4mods_agentv3.controllers.ModsInstalledController;
import com.mymix.s4mods_agentv3.controllers.ModsOnlineController;
import com.mymix.s4mods_agentv3.models.Mod;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.List;

public class InstalledModsListActivity extends ModsListActivity
{
    private static JLabel category_name = null;
    private JLabel no_mods = new JLabel("<html><center>Ни один мод не установлен.<br>Пора это исправить!</center></html>");

    public InstalledModsListActivity()
    {
    }

    public InstalledModsListActivity(int filter)
    {

    }

    @Override
    public int init()
    {
        // если не выбрана категория, значит показаны все категории
        if (category_name == null)
            category_name = new JLabel("Все моды");

        // Загрузка компонентов
        List<Mod> list = ModsController.getInstalledModsList();
        if (list.isEmpty())
            mods.add(no_mods);
        else
        {
            list.forEach(this::addMod);
            ModsOnlineController.startBGInstalledLoading();
        }
        loadFilters();


        // -- РАЗМЕТКА --
        // TOP MENU
        top_menu.setLayout(new GridLayout(2, 1));
        UIDecorator.setComponentTransparent(top_menu);
        // верхняя панель:
        JPanel top_panel = new JPanel(new BorderLayout());
        UIDecorator.setComponentTransparent(top_panel);
        // - меню (слева)
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        UIDecorator.setComponentTransparent(menu);
        //   * ссылки:
        menu.add(UIDecorator.normalizeElementRepaint(
                UIDecorator.createPrettyButton(
                        "ГЛАВНАЯ",
                        l -> Main.activity(new StartActivity())),
                this
        ));
        menu.add(UIDecorator.normalizeElementRepaint(
                UIDecorator.createPrettyButton(
                        "Все моды",
                        l -> Main.activity(new OnlineModsListActivity())),
                this
        ));
        // - ENDof(меню)
        top_panel.add(menu, BorderLayout.CENTER);

        // ENDof(верхняя панель)
        top_menu.add(top_panel);

        // выбранные фильтры (панель 2-го уровня):
        JPanel filters = new JPanel(new BorderLayout());
        UIDecorator.setComponentTransparent(filters);
        JPanel category_name_container = new JPanel(new FlowLayout());
        UIDecorator.setComponentTransparent(category_name_container);
        category_name_container.add(category_name);
        filters.add(category_name_container, BorderLayout.CENTER);
        // на эту же панель добавляем кнопку показа загрузок
        JButton downloading_toggle = new JButton("Показать загрузки");
        UIDecorator.normalizeElementRepaint(downloading_toggle, this);
        downloading_toggle.addActionListener(l ->
        {
            dp_visible = !dp_visible;
            downloading_progress_container.setVisible(dp_visible);
            downloading_toggle.setText((dp_visible ? "Скрыть" : "Показать") + " загрузки");
        });
        filters.add(downloading_toggle, BorderLayout.EAST);
        top_menu.add(filters);
        // ENDof(TOP MENU)

        // DOWNLOADING PROGRESS
        downloading_progress.setLayout(new BoxLayout(downloading_progress, BoxLayout.Y_AXIS));
        UIDecorator.setComponentTransparent(downloading_progress);
        UIDecorator.setComponentTransparent(downloading_progress_container);
        downloading_progress.add(no_downloading);
        // Добавляем обёртку для загрузок
        downloading_progress_container.add(downloading_progress, BorderLayout.CENTER);
        // Кнопка очистки загрузок на обёртке
        JButton clear_downloads = new JButton("Очистить загрузки");
        UIDecorator.normalizeElementRepaint(clear_downloads, this);
        downloading_progress_container.add(clear_downloads, BorderLayout.SOUTH);
        clear_downloads.addActionListener(l ->
        {
            // хз почему, но работает только так
            synchronized (downloads)
            {
                try
                {
                    // тут возникает Exception
                    downloads.forEach(el ->
                    {
                        if (el.done())
                        {
                            el.remove();
                            downloads.remove(el);
                        }
                    });
                }
                catch (ConcurrentModificationException ex)
                {
                    // а тут уже нет. видимо, после Exception перехватывается контроль над downloads
                    /*downloads.forEach(el ->
                    {
                        if (el.done())
                        {
                            el.remove();
                            downloads.remove(el);
                        }
                    });*/
                }
            }
            // обновляем загрузочную панель
            updateDownloadingPanel();
        });
        updateDownloadingPanel();

        return 0;
    }

    protected void loadFilters()
    {
        addFilter("Все моды", 0);
        addFilter("Только активные",  1);
        addFilter("Только неактивные", 2);
    }

    private void addFilter(String name, int filter)
    {
        JButton b = UIDecorator.createPrettyButton(name, l ->
        {
            category_name.setText(name);
            Main.activity(new InstalledModsListActivity(filter));
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
        UIDecorator.normalizeElementRepaint(b, this);
    }

    private void updateMods()
    {
        if (mods.getComponents().length == 0)
        {
            mods.add(no_mods);
        }
        repaint();
    }

    public void addMod(Mod mod)
    {
        JPanel mod_panel = new JPanel(new GridBagLayout()),
                top_panel = new JPanel(new BorderLayout()),
                name_group = new JPanel(new FlowLayout()),
                control_group = new JPanel(new FlowLayout()),
                image_panel = new JPanel(new FlowLayout());
        JButton name = new JButton(mod.name()),
                remove = new JButton(),
                on = new JButton(),
                off = new JButton(),
                image = new JButton();
        JTextPane desc = new JTextPane();

        UIDecorator.setComponentTransparent(mod_panel);

        image.addActionListener(l ->
        {
            this.repaint();
            if (null != mod.link() && !mod.link().equals(""))
            {
                SliderActivity slider = new SliderActivity(mod.link());
                slider.setActive(Main.getThis());
            }
            this.repaint();
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.ipadx = 10;
        c.ipady = 5;
        if (Main.internet_connection())
        {
            image_panel.setPreferredSize(new Dimension(150, 155));
            image_panel.add(image);
            mod_panel.add(image_panel, c);
        }

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        name.addActionListener(l -> Main.activity(new OneModActivity(mod)));
        name.setContentAreaFilled(false);
        name_group.add(name);

        UIDecorator.makeIconButton(remove, Images.bdel(), 20, 20);
        remove.addActionListener(l ->
        {
            Main.delete(mod);
            mods.remove(mod_panel);
            updateMods();
        });
        control_group.add(remove);

        UIDecorator.makeIconButton(on, Images.benable(), 20, 20);
        UIDecorator.makeIconButton(off, Images.bdisable(), 20, 20);
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

        if (!mod.installed() || "".equals(mod.filename()))
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

        //makeAdaptiveIconButton(image, mod.image(), Math.max(desc.getSize(null).height, 155));

        c.gridx = 2;
        c.gridy = 1;
        desc.setEditable(false);
        desc.setText(mod.description());
        int w_max = Main.getWidth();
        w_max -= filters_panel.getMaximumSize().width + 400;
        Dimension s = new Dimension(w_max, 155);
        desc.setPreferredSize(s);
        //JScrollPane desc_container = new JScrollPane(desc);
        //desc_container.setPreferredSize(s);
        //desc_container.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //mod_panel.add(desc_container, c);
        mod_panel.add(desc, c);

        mods.add(mod_panel, conf);


        // ДЛЯ ВОЗМОЖНОСТИ ОБНОВЛЕНИЯ В ФОНОВОМ РЕЖИМЕ
        mods_desc.put(mod.link(), desc);
        mods_imgs.put(mod.link(), image);


        mods_dl.put(mod.link(), new JComponent[]{remove, on, off});

        UIDecorator.normalizeElementRepaint(image, this);
    }

    @Override
    public void setInactive(Container contentPane)
    {
        super.setInactive(contentPane);
        removeAll();
        if (Main.current_activity().getClass() != getClass())
            ModsOnlineController.stopBGLoading();
    }
}
