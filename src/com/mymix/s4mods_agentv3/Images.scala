package com.mymix.s4mods_agentv3

import javax.swing.ImageIcon

object Images
{
    val bdownload: ImageIcon = loadBtnIcon("download")
    val benable: ImageIcon = loadBtnIcon("enable")
    val bdel: ImageIcon = loadBtnIcon("del")
    val bdisable: ImageIcon = loadBtnIcon("disable")

    val btimes: ImageIcon = loadBtnIcon("times")
    val bstop: ImageIcon = loadBtnIcon("stop")
    val bpause: ImageIcon = loadBtnIcon("pause")
    val bresume: ImageIcon = loadBtnIcon("resume")

    val bdpause: ImageIcon= loadDisabledBtnIcon("pause")
    val bdresume: ImageIcon = loadDisabledBtnIcon("resume")



    def loadImageFromRes(img_path: String): ImageIcon =
    {
        println(img_path)
        new ImageIcon(getClass.getResource("res/icons/" + img_path))
    }
    def loadBtnIcon(icon_name: String): ImageIcon = loadImageFromRes("btn-" + icon_name + ".png")
    def loadDisabledBtnIcon(icon_name: String): ImageIcon = loadBtnIcon(icon_name + "-disabled")
}
