package com.github.jagieloadrian.darksoulsprogressbar.ui

import javax.swing.ImageIcon

class FireSpinner {
    private val fireSpinner: ImageIcon = ImageIcon(javaClass.getResource("/gif/bonfire_darksouls_small.gif"))

    fun getIcon(): ImageIcon {
        return fireSpinner
    }
}