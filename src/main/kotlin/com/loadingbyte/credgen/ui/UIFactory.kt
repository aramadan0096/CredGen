package com.loadingbyte.credgen.ui

import com.loadingbyte.credgen.ui.comms.MasterCtrlComms
import com.loadingbyte.credgen.ui.comms.UIFactoryComms
import com.loadingbyte.credgen.ui.comms.WelcomeCtrlComms
import com.loadingbyte.credgen.ui.ctrl.MasterCtrl
import com.loadingbyte.credgen.ui.ctrl.WelcomeCtrl
import com.loadingbyte.credgen.ui.view.welcome.WelcomeFrame


class UIFactory : UIFactoryComms {

    override fun master(): MasterCtrlComms =
        MasterCtrl(this)

    override fun welcomeCtrlViewPair(masterCtrl: MasterCtrlComms): WelcomeCtrlComms {
        val welcomeCtrl = WelcomeCtrl(masterCtrl)
        val welcomeView = WelcomeFrame(welcomeCtrl)
        welcomeCtrl.supplyView(welcomeView)
        return welcomeCtrl
    }

}
