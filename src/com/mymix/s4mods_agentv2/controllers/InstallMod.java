package com.mymix.s4mods_agentv2.controllers;

import com.mymix.s4mods_agentv2.Constants;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class InstallMod
{
    public InstallMod(String href)
    {
        try
        {
            URL url = new URL(Constants.URL() + href);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            int completeFileSize = httpConnection.getContentLength();
            JProgressBar progressBar = new JProgressBar(0, completeFileSize);

            String path = System.getProperty("user.home") + "\\Documents\\Electronic Arts\\";
            File ea_dir = new File(path);
            String[] sims = ea_dir.list((dir, name) -> name.matches("^The Sims 4.*"));

            if (sims != null)
            {
                if (sims.length > 1)
                {

                }
                else
                    path += sims[0];
            }

            path += "\\Mods\\" + httpConnection.getHeaderField("Content-Disposition")
                    .replace("attachment; filename=", "");

            InputStream in = new BufferedInputStream(httpConnection.getInputStream());
            OutputStream out = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int count = in.read(buffer);
            while (count != -1)
            {
                if (progressBar.getValue() + 1024 <= progressBar.getMaximum())
                    progressBar.setValue(progressBar.getValue() + 1024);
                else
                    progressBar.setValue(progressBar.getMaximum());

                out.write(buffer, 0, count);
                count = in.read(buffer);
            }

            in.close();
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
