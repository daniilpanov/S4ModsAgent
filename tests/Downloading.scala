import java.awt.FlowLayout
import java.net.{HttpURLConnection, URL}
import java.io.{BufferedInputStream, FileOutputStream}

import javax.swing.{JFrame, JProgressBar}

object Downloading
{
    val frame = new JFrame("test")
    var progressBar: JProgressBar = _

    def main(args: Array[String]): Unit =
    {
        //val url = new URL("https://sims4pack.ru/materials/img/20210815071632572.jpg")
        val url = new URL(com.mymix.s4mods_agentv2.Constants.URL + "/go/8084")
        val httpConnection = url.openConnection.asInstanceOf[HttpURLConnection]
        val completeFileSize = httpConnection.getContentLength
        progressBar = new JProgressBar(0, completeFileSize)
        //progressBar.setSize(800, 25)
        val fname = httpConnection.getHeaderField("Content-Disposition")
            .replace("attachment; filename=", "")

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setSize(800, 600)
        frame.getContentPane.setLayout(new FlowLayout(FlowLayout.CENTER))
        frame.getContentPane.add(progressBar)
        frame.setVisible(true)

        val in = new BufferedInputStream(httpConnection.getInputStream)
        //val out = new FileOutputStream("test-res/loaded.jpg")
        val out = new FileOutputStream("test-res/" + fname)

        println("Downloading...")

        val buffer = new Array[Byte](1024)
        var count = in.read(buffer)
        while (count != -1)
        {
            if (progressBar.getValue + 1024 <= progressBar.getMaximum)
                progressBar.setValue(progressBar.getValue + 1024)
            else
                progressBar.setValue(progressBar.getMaximum)

            out.write(buffer, 0, count)
            count = in.read(buffer)
        }

        println("Downloaded successful!")

        in.close()
        out.close()
    }
}
