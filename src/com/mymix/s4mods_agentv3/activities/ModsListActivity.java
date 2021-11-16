package com.mymix.s4mods_agentv3.activities;

import com.mymix.s4mods_agentv3.Images;
import com.mymix.s4mods_agentv3.Main;
import com.mymix.s4mods_agentv3.UIDecorator;
import com.mymix.s4mods_agentv3.models.Mod;
import com.mymix.s4mods_agentv3.models.ModInstaller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

abstract public class ModsListActivity extends Activity
{
    JPanel top_menu = new JPanel(new FlowLayout(FlowLayout.LEFT)),
            filters_panel = new JPanel(),
            mods = new JPanel();
    JScrollPane filters_scroll = new JScrollPane(filters_panel),
            mods_scroll = new JScrollPane(mods);
    GridBagConstraints conf = new GridBagConstraints();


    // ID = Mod.link
    HashMap<String, JTextPane> mods_desc = new HashMap<>();
    HashMap<String, JComponent[]> mods_dl = new HashMap<>();
    HashMap<String, JButton> mods_imgs = new HashMap<>();


    static JPanel downloading_progress = new JPanel();
    static JPanel downloading_progress_container = new JPanel(new BorderLayout());
    static boolean dp_visible = false;
    static JLabel no_downloading = new JLabel("В данный момент ничего не загружается");
    static int downloading_counter = 0;
    static final java.util.List<ModInstaller> downloads = new ArrayList<>();


    ModsListActivity()
    {
        super();
        filters_panel.setLayout(new BoxLayout(filters_panel, BoxLayout.Y_AXIS));
        filters_scroll.getVerticalScrollBar().addAdjustmentListener(e -> this.repaint());

        mods.setLayout(new GridBagLayout());
        mods_scroll.getVerticalScrollBar().addAdjustmentListener(e -> this.repaint());
        conf.fill = GridBagConstraints.BOTH;
        conf.anchor = GridBagConstraints.BASELINE;

        conf.gridx = 1;
        conf.gridy = GridBagConstraints.RELATIVE;
        conf.weightx = 1;
        conf.weighty = 1;
        conf.ipadx = 10;
        conf.ipady = 25;
    }

    @Override
    public int init()
    {
        // DOWNLOADING PROGRESS
        downloading_progress.setLayout(new BoxLayout(downloading_progress, BoxLayout.Y_AXIS));
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

    abstract protected void loadFilters();

    @Override
    public void setActive(Container contentPane)
    {
        setLayout(new BorderLayout());
        // menu
        add(top_menu, BorderLayout.NORTH);
        // filters
        filters_panel.setMaximumSize(new Dimension(300, 2000));
        filters_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        filters_scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(filters_scroll, BorderLayout.WEST);
        // mods
        mods_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mods_scroll.setWheelScrollingEnabled(true);
        mods_scroll.getVerticalScrollBar().setUnitIncrement(10);
        add(mods_scroll, BorderLayout.CENTER);
        Timer t = new Timer(500, e ->
        {
            mods_scroll.getVerticalScrollBar().setValue(
                    mods_scroll.getVerticalScrollBar().getMinimum()
            );

            repaint();
        });
        t.setRepeats(false);
        t.start();
        //
        add(downloading_progress_container, BorderLayout.EAST);
        downloading_progress_container.setVisible(dp_visible);

        JPanel p = new JPanel(new FlowLayout());
        Border b = new ImagedBorder();
        this.setBorder(b);

        p.add(this);
        //contentPane.add(p);
        contentPane.add(this);

        Main.repaint();
    }

    class ImagedBorder implements Border
    {
        private Insets ins = new Insets(0, 100, 0, 100);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
        {
            g.clearRect(0, 0, 100, Main.getHeight());
            g.drawImage(Images.lbg().getImage(), 0, 0, null);
            g.clearRect(Main.getWidth() - 100, 0, Main.getWidth(), Main.getHeight());
            g.drawImage(Images.rbg().getImage(), 0, 0, null);
        }

        @Override
        public Insets getBorderInsets(Component c)
        {
            return ins;
        }

        @Override
        public boolean isBorderOpaque()
        {
            return false;
        }
    }

    abstract public void addMod(Mod mod);


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
}
