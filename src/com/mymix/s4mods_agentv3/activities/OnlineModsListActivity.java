package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Constants;
import com.mymix.s4mods_agentv3.Images;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.components.ModDescScrollPane;
import com.mymix.s4mods_agentv3.controllers.ModsController;
import com.mymix.s4mods_agentv3.controllers.ModsInstalledController;
import com.mymix.s4mods_agentv3.controllers.ModsOnlineController;
import com.mymix.s4mods_agentv3.models.CategoriesCollection;
import com.mymix.s4mods_agentv3.models.Category;
import com.mymix.s4mods_agentv3.models.Mod;
import com.mymix.s4mods_agentv3.models.ModInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import com.mymix.s4mods_agentv3.UIDecorator;

public class OnlineModsListActivity extends ModsListActivity
{
    private static JLabel category_name = null;

    // ID = Mod.link
    private HashMap<String, JTextPane> mods_desc = new HashMap<>();
    private HashMap<String, JComponent[]> mods_dl = new HashMap<>();
    private HashMap<String, JButton> mods_imgs = new HashMap<>();

    private static JPanel downloading_progress = new JPanel();
    private static JPanel downloading_progress_container = new JPanel(new BorderLayout());
    private static boolean dp_visible = false;
    private static JLabel no_downloading = new JLabel("В данный момент ничего не загружается");
    private static int downloading_counter = 0;
    private static final List<ModInstaller> downloads = new ArrayList<>();


    public OnlineModsListActivity()
    {
        this("");
    }

    public OnlineModsListActivity(String path)
    {
        super();
        ModsController.changePath(path);
    }

    public OnlineModsListActivity(int page)
    {
        super();
        ModsController.changePage(page);
    }

    @Override
    public int init()
    {
        Main.checkInternetConnection();
        if (!Main.internet_connection())
            return 1;

        // если не выбрана категория, значит показаны все категории
        if (category_name == null)
            category_name = new JLabel("Все моды");

        // Загрузка компонентов
        List<Mod> list = ModsController.getOnlineModsList();
        if (list.isEmpty())
            mods.add(new JLabel("Ничего не найдено"));
        else
        {
            loadPagination();
            list.forEach(this::addMod);
            ModsOnlineController.startBGLoading();
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
                menu
        ));
        menu.add(UIDecorator.normalizeElementRepaint(
                UIDecorator.createPrettyButton(
                        "Установленные моды",
                        l -> Main.activity(new InstalledModsListActivity())),
                menu
        ));
        // - ENDof(меню)
        top_panel.add(menu, BorderLayout.CENTER);

        // - поиск (справа)
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        UIDecorator.setComponentTransparent(search);
        //   * поле для поиска (берём значение по умолчанию из указанного в ModsController.path пути)
        String search_str = (ModsController.path() != null && ModsController.path().matches("^/search\\?q="))
                ? ModsController.path().replace("/search?q=", "") : "";
        JTextField search_input = new JTextField(search_str);
        search_input.setColumns(25);
        search_input.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        search_input.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                super.keyPressed(e);
                if (e.getKeyCode() == 10)
                {
                    category_name.setText("Поиск: " + search_input.getText());
                    Main.activity(new OnlineModsListActivity("/search?q=" + search_input.getText()));
                }
            }
        });
        //   * кнопка поиска
        JButton search_go = new JButton("Поиск");
        UIDecorator.normalizeElementRepaint(search_go, this);
        search_go.addActionListener(l ->
        {
            category_name.setText("Поиск: " + search_input.getText());
            Main.activity(new OnlineModsListActivity("/search?q=" + search_input.getText()));
        });
        search.add(search_input);
        search.add(search_go);
        // - ENDof(поиск)
        top_panel.add(search, BorderLayout.EAST);

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

    @Override
    public void initError(int code)
    {
        super.initError(code);

        Main.checkInternetConnection();
        if (code == 1)
        {
            if (JOptionPane.showConfirmDialog(Main.getThis(),
                    "<html><h2 style='color:red'>" +
                            "ERROR: No internet connection!</h2>" +
                            "Повторить попытку?" +
                            "</html>",
                    "Connection Lost",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
                    ) == JOptionPane.YES_OPTION)
            {
                Main.activity(new OnlineModsListActivity());
            }
        }
    }

    @Override
    public void setActive(Container contentPane)
    {
        super.setActive(contentPane);

        add(downloading_progress_container, BorderLayout.EAST);
        downloading_progress_container.setVisible(dp_visible);
    }

    public void updateImage(String mod_link, String img_path)
    {
        if (!mods_imgs.containsKey(mod_link))
            return;

        UIDecorator.makeAdaptiveIconButton(mods_imgs.get(mod_link), img_path, 155);

        mods_imgs.remove(mod_link);
    }

    public void updateDLnDesc(String mod_link, String desc, String dl)
    {
        if (!mods_desc.containsKey(mod_link) && !mods_dl.containsKey(mod_link))
            return;

        mods_desc.get(mod_link).setText(desc);

        mods_desc.remove(mod_link);
        mods_dl.remove(mod_link);
    }

    public void updateDownloadingPanel()
    {
        //Constants.log(downloading_counter);
        no_downloading.setVisible(downloading_counter == 0);
    }

    public ModInstaller addDownloading(ModInstaller installer)
    {
        synchronized (downloads)
        {
            ++ downloading_counter;
            downloads.add(installer);
            updateDownloadingPanel();
        }

        return installer;
    }

    public void endDownloading(ModInstaller installer)
    {
        synchronized (downloads)
        {

        }
    }

    public void removeDownloading(ModInstaller installer)
    {
        synchronized (downloads)
        {
            -- downloading_counter;
            downloads.remove(installer);
            updateDownloadingPanel();
        }
    }

    private void loadPagination()
    {
        // Отображение кнопки навигации НАЗАД
        if (ModsController.page() > 1)
        {
            JButton pre = new JButton("Назад");
            UIDecorator.normalizeElementRepaint(pre, this);
            pre.addActionListener(l ->
            {
                Main.activity(new OnlineModsListActivity(ModsController.page() - 1));
            });
            pagination.add(pre);
        }

        // pm - pagination_max, pc - pagination_current
        int pm = ModsOnlineController.getPagination(), pc = ModsOnlineController.pagination_current();

        // Загрузка страниц группы ДО
        if (pc > 1)
        {
            // отображение первой страницы
            JButton pb_first = new JButton(String.valueOf(1));
            UIDecorator.normalizeElementRepaint(pb_first, this);
            pb_first.addActionListener(l ->
            {
                Main.activity(new OnlineModsListActivity(1));
            });
            pagination.add(pb_first);

            // с какого элемента начинать (выводим 4 страницы группы ДО)
            int begin = (pc > 6) ? pc - 4 : 2;
            // отображаем пробелы
            if (begin > 2)
                pagination.add(new JLabel("..."));

            // выводим основные страницы группы ДО
            for (int i = begin; i < pc; ++ i)
            {
                JButton pb = new JButton(String.valueOf(i));
                UIDecorator.normalizeElementRepaint(pb, this);
                final int page = i;
                pb.addActionListener(l ->
                {
                    Main.activity(new OnlineModsListActivity(page));
                });
                pagination.add(pb);
            }
        }

        // Выводим текущую страницу
        JButton pb_curr = new JButton(String.valueOf(pc));
        UIDecorator.normalizeElementRepaint(pb_curr, this);
        pb_curr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 3, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        pagination.add(pb_curr);

        // Загрузка страниц группы ПОСЛЕ
        if (pc < pm)
        {
            // до какого элемента выводить (выводим 4 страницы группы ПОСЛЕ)
            int end = (pm - pc > 5) ? pc + 4 : pm - 1;

            // выводим основные страницы группы ПОСЛЕ
            for (int i = pc + 1; i <= end; ++ i)
            {
                JButton pb = new JButton(String.valueOf(i));
                UIDecorator.normalizeElementRepaint(pb, this);
                final int page = i;
                pb.addActionListener(l ->
                {
                    Main.activity(new OnlineModsListActivity(page));
                });
                pagination.add(pb);
            }
            //  отображаем пробелы
            if (pm > pc + 5)
                pagination.add(new JLabel("..."));
            // отображение первой страницы
            JButton pb_end = new JButton(String.valueOf(pm));
            UIDecorator.normalizeElementRepaint(pb_end, this);
            pb_end.addActionListener(l ->
            {
                Main.activity(new OnlineModsListActivity(pm));
            });
            pagination.add(pb_end);
        }

        // Отображение кнопки навигации ДАЛЕЕ
        if (ModsOnlineController.pagination_current() < ModsOnlineController.pagination_max())
        {
            JButton next = new JButton("Далее");
            UIDecorator.normalizeElementRepaint(next, this);
            next.addActionListener(l ->
            {
                Main.activity(new OnlineModsListActivity(ModsController.page() + 1));
            });
            pagination.add(next);
        }
    }

    protected void loadFilters()
    {
        CategoriesCollection list = ModsController.getFilters();

        addFilter(new Category(0, "Все моды", null, 0));
        list.forEach(this::addFilter);
    }

    private void addFilter(Category c)
    {
        String n = (c.parent() > 0 ? " - " + c.name() : c.name());
        JButton b = UIDecorator.createPrettyButton(n, l ->
        {
            category_name.setText(c.name());
            Main.activity(new OnlineModsListActivity(c.link()));
        });

        if (category_name.getText().equals(c.name()))
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

    public void addMod(Mod mod)
    {
        ModsInstalledController.isModInstalled(mod);
        JPanel mod_panel = new JPanel(new GridBagLayout()),
                top_panel = new JPanel(new BorderLayout()),
                name_group = new JPanel(new FlowLayout()),
                control_group = new JPanel(new FlowLayout()),
                image_panel = new JPanel(new FlowLayout());
        JLabel installed = new JLabel("(установлен)");
        JButton name = (JButton) UIDecorator.normalizeElementRepaint(
                    new JButton(mod.name()), this),
                download = (JButton) UIDecorator.normalizeElementRepaint(
                        new JButton(), this),
                remove = (JButton) UIDecorator.normalizeElementRepaint(
                        new JButton(), this),
                on = (JButton) UIDecorator.normalizeElementRepaint(
                        new JButton(), this),
                off = (JButton) UIDecorator.normalizeElementRepaint(
                        new JButton(), this),
                image = (JButton) UIDecorator.normalizeElementRepaint(
                        new JButton(), this);
        JTextPane desc = new JTextPane();

        UIDecorator.setComponentTransparent(mod_panel);

        image.addActionListener(l ->
        {
            this.repaint();
            SliderActivity slider = new SliderActivity(mod.link());
            slider.setActive(Main.getThis());
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
        image_panel.setPreferredSize(new Dimension(150, 155));
        image_panel.add(image);
        mod_panel.add(image_panel, c);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        name.addActionListener(l -> Main.activity(new OneModActivity(mod)));
        name.setContentAreaFilled(false);
        name_group.add(installed);
        name_group.add(name);

        UIDecorator.makeIconButton(download, Images.bdownload(), 20, 20);
        UIDecorator.makeIconButton(remove, Images.bdel(), 20, 20);
        download.addActionListener(l ->
        {
            addDownloading(Main.install(mod, downloading_progress)).installed(() ->
            {
                installed.setText("(установлен)");
                remove.setVisible(true);
                download.setVisible(false);

                off.setVisible(true);
                on.setVisible(false);
            }).interrupted(() ->
            {
                installed.setText("(установлен)");
                download.setEnabled(true);
            });

            installed.setText("(устанавливается...)");
            installed.setVisible(true);
            download.setEnabled(false);
        });
        remove.addActionListener(l ->
        {
            Main.delete(mod);
            download.setVisible(true);
            installed.setVisible(false);
            remove.setVisible(false);

            on.setVisible(false);
            off.setVisible(false);

            download.setEnabled(true);
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
            Constants.log(mod);
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
        JScrollPane desc_container = new ModDescScrollPane(desc, mods_scroll);
        desc_container.setPreferredSize(s);

        mod_panel.add(desc_container, c);
        //mod_panel.add(desc, c);

        mods.add(mod_panel, conf);


        // ДЛЯ ВОЗМОЖНОСТИ ОБНОВЛЕНИЯ В ФОНОВОМ РЕЖИМЕ
        mods_desc.put(mod.link(), desc);
        mods_imgs.put(mod.link(), image);


        mods_dl.put(mod.link(), new JComponent[]{download, remove, installed, on, off});
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
