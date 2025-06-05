package com.loadingbyte.credgen.ui.comms


interface UIFactoryComms {

    fun master(): MasterCtrlComms
    fun welcomeCtrlViewPair(masterCtrl: MasterCtrlComms): WelcomeCtrlComms

}
