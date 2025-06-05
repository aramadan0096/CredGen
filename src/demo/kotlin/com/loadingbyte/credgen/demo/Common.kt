package com.loadingbyte.credgen.demo

import java.util.*


fun l10nDemo(key: String): String =
    ResourceBundle.getBundle("l10n.demo").getString(key)
