package com.loadingbyte.credgen.ui.view.welcome

import com.formdev.flatlaf.FlatClientProperties.STYLE
import com.formdev.flatlaf.FlatClientProperties.STYLE_CLASS
import com.formdev.flatlaf.icons.FlatAbstractIcon
import com.formdev.flatlaf.ui.FlatRoundBorder
import com.loadingbyte.credgen.common.Severity
import com.loadingbyte.credgen.common.l10n
import com.loadingbyte.credgen.imaging.Color4f
import com.loadingbyte.credgen.project.Opt
import com.loadingbyte.credgen.projectio.service.Account
import com.loadingbyte.credgen.projectio.service.SERVICES
import com.loadingbyte.credgen.projectio.service.Service
import com.loadingbyte.credgen.ui.*
import com.loadingbyte.credgen.ui.comms.PreferencesCard
import com.loadingbyte.credgen.ui.comms.WelcomeCtrlComms
import com.loadingbyte.credgen.ui.helper.*
import net.miginfocom.swing.MigLayout
import java.awt.*
import java.nio.file.Path
import java.util.*
import javax.swing.*
import kotlin.io.path.Path
import kotlin.jvm.optionals.getOrNull


class PreferencesPanel(private val welcomeCtrl: WelcomeCtrlComms) : JPanel() {

    // ========== ENCAPSULATION LEAKS ==========
    @Deprecated("ENCAPSULATION LEAK") val leakedStartAddAccountButton: JButton
    @Deprecated("ENCAPSULATION LEAK") val leakedStartAddOverlayButton: JButton
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgAccountLabelWidget get() = configureAccountForm.labelWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgAccountServiceWidget get() = configureAccountForm.serviceWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgAccountEstButton get() = configureAccountEstablishButton
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgOverlayTypeWidget get() = configureOverlayForm.typeWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgOverlayNameWidget get() = configureOverlayForm.nameWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgOverlayLinesHWidget get() = configureOverlayForm.linesHWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgOverlayDoneButton get() = configureOverlayDoneButton
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgTemplateNameWidget
        get() = configureDeliveryDestTemplateForm.nameWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgTemplateStrWidget
        get() = configureDeliveryDestTemplateForm.templateStrWidget
    @Deprecated("ENCAPSULATION LEAK") val leakedCfgTemplateDoneButton get() = configureDeliveryDestTemplateDoneButton
    // =========================================

    val startPreferencesForm: PreferencesForm

    private val cards = CardLayout().also { layout = it }

    private val startLowerPanel: JPanel
    private val startAccountsPanel: JPanel
    private val startAccountsRemovalButtons = HashMap<Account, JButton>()
    private val startOverlaysPanel: JPanel
    private val startDeliveryDestTemplatesPanel: JPanel

    private val configureAccountForm: ConfigureAccountForm
    private val configureAccountEstablishButton: JButton

    private val establishAccountMsgTextArea: JTextArea
    private val establishAccountErrorTextArea: JTextArea
    private val establishAccountResponseTextArea: JTextArea

    private val configureOverlayForm: ConfigureOverlayForm
    private val configureOverlayDoneButton: JButton
    private val configureOverlayApplyButton: JButton

    private val configureDeliveryDestTemplateForm: ConfigureDeliveryDestTemplateForm
    private val configureDeliveryDestTemplateDoneButton: JButton
    private val configureDeliveryDestTemplateApplyButton: JButton

    init {
        startPreferencesForm = PreferencesForm(welcomeCtrl).apply {
            background = null
        }

        val startAddAccountButton = JButton(l10n("ui.preferences.accounts.add"), ADD_ICON).apply {
            addActionListener { welcomeCtrl.preferences_start_onClickAddAccount() }
        }
        startAccountsPanel = JPanel(MigLayout("insets 0, wrap 2, fillx", "[sg, fill][sg, fill]")).apply {
            background = null
        }

        val startAddOverlayButton = JButton(l10n("ui.preferences.overlays.add"), ADD_ICON).apply {
            addActionListener { welcomeCtrl.preferences_start_onClickAddOverlay() }
        }
        startOverlaysPanel = JPanel(MigLayout("insets 0, wrap 2, fillx", "[sg, fill][sg, fill]")).apply {
            background = null
        }

        val startAddDeliveryDestTemplButton =
            JButton(l10n("ui.preferences.deliveryDestTemplates.add"), ADD_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickAddDeliveryDestTemplate() }
            }
        startDeliveryDestTemplatesPanel = JPanel(MigLayout("insets 0, wrap, fillx", "[fill]")).apply {
            background = null
        }

        startLowerPanel = JPanel(MigLayout("insets 0, wrap, hidemode 3")).apply {
            background = null
            add(JSeparator(), "growx, pushx, gapy unrel unrel")
            add(startAddAccountButton)
            add(startAccountsPanel, "growx, pushx, gaptop rel")
            add(JSeparator(), "growx, pushx, gapy unrel unrel")
            add(startAddOverlayButton)
            add(startOverlaysPanel, "growx, pushx, gaptop rel")
            add(JSeparator(), "growx, pushx, gapy unrel unrel")
            add(startAddDeliveryDestTemplButton)
            add(startDeliveryDestTemplatesPanel, "growx, pushx, gaptop rel")
        }
        val startWidgetsPanel = JPanel(MigLayout("insets 0 0 0 10, wrap, gapy 0")).apply {
            background = null
            add(startPreferencesForm, "growx, pushx")
            add(startLowerPanel, "grow, push")
        }
        val startScrollPane = JScrollPane(startWidgetsPanel).apply {
            border = null
            background = null
            viewport.background = null
            verticalScrollBar.unitIncrement = 10
            verticalScrollBar.blockIncrement = 100
        }
        val startPanel = JPanel(MigLayout("insets 20 20 20 10, wrap")).apply {
            background = null
            add(newLabelTextArea(l10n("ui.preferences.msg")), "hmin pref, growx")
            add(startScrollPane, "grow, push, gaptop para")
        }

        configureAccountForm = ConfigureAccountForm().apply {
            background = null
        }
        val configureAccountCancelButton = JButton(l10n("cancel"), CROSS_ICON).apply {
            addActionListener { welcomeCtrl.preferences_configureAccount_onClickCancel() }
        }
        configureAccountEstablishButton = JButton().apply {
            margin = Insets(1, 1, 1, margin.right)
            iconTextGap = margin.right
            putClientProperty(STYLE_CLASS, "large")
        }
        configureAccountEstablishButton.addActionListener {
            welcomeCtrl.preferences_configureAccount_onClickEstablish(
                configureAccountForm.labelWidget.value,
                configureAccountForm.serviceWidget.value.get(),
                configureAccountForm.serverWidget.value
            )
        }
        configureAccountForm.onChange(configureAccountForm.labelWidget)  // Run validation
        val configureAccountPanel = JPanel(MigLayout("insets 20, wrap")).apply {
            background = null
            add(newLabelTextArea(l10n("ui.preferences.accounts.configure.prompt")), "growx")
            add(configureAccountForm, "grow, push, gaptop para")
            add(configureAccountCancelButton, "split 2, right, bottom")
            add(configureAccountEstablishButton, "gapleft unrel, hidemode 3")
        }

        establishAccountMsgTextArea = newLabelTextArea()
        establishAccountErrorTextArea = newLabelTextArea().apply {
            putClientProperty(STYLE, "foreground: $PALETTE_RED")
        }
        establishAccountResponseTextArea = newLabelTextArea().apply {
            putClientProperty(STYLE, "foreground: $PALETTE_RED")
        }
        val establishAccountCancelButton = JButton(l10n("cancel"), CROSS_ICON).apply {
            addActionListener { welcomeCtrl.preferences_establishAccount_onClickCancel() }
        }
        val establishAccountPanel = JPanel(MigLayout("insets 20, wrap", "", "[][][]push[]")).apply {
            background = null
            add(establishAccountMsgTextArea, "growx, pushx")
            add(establishAccountErrorTextArea, "growx")
            add(establishAccountResponseTextArea, "growx")
            add(establishAccountCancelButton, "right")
        }

        configureOverlayForm = ConfigureOverlayForm().apply {
            background = null
        }
        val configureOverlayCancelButton = JButton(l10n("cancel"), CROSS_ICON).apply {
            addActionListener { welcomeCtrl.preferences_configureOverlay_onClickCancel() }
        }

        fun configureOverlayDoneOrApplyCallback(done: Boolean) {
            welcomeCtrl.preferences_configureOverlay_onClickDoneOrApply(
                done,
                configureOverlayForm.typeWidget.value,
                configureOverlayForm.nameWidget.value,
                configureOverlayForm.aspectRatioHWidget.value,
                configureOverlayForm.aspectRatioVWidget.value,
                configureOverlayForm.linesColorWidget.value.run { if (isActive) value else null },
                configureOverlayForm.linesHWidget.value,
                configureOverlayForm.linesVWidget.value,
                configureOverlayForm.imageFileWidget.value,
                configureOverlayForm.imageUnderlayWidget.value
            )
        }
        configureOverlayDoneButton = JButton(l10n("ok"), CHECK_ICON).apply {
            addActionListener { configureOverlayDoneOrApplyCallback(true) }
        }
        configureOverlayApplyButton = JButton(l10n("apply"), CHECK_ICON).apply {
            addActionListener { configureOverlayDoneOrApplyCallback(false) }
        }
        val configureOverlayPanel = JPanel(MigLayout("insets 20, wrap")).apply {
            background = null
            add(newLabelTextArea(l10n("ui.preferences.overlays.configure.prompt")), "growx")
            add(configureOverlayForm, "grow, push, gaptop para")
            add(configureOverlayCancelButton, "split 3, right")
            add(configureOverlayDoneButton)
            add(configureOverlayApplyButton)
        }

        configureDeliveryDestTemplateForm = ConfigureDeliveryDestTemplateForm().apply {
            background = null
        }
        val configureDeliveryDestTemplateCancelButton = JButton(l10n("cancel"), CROSS_ICON).apply {
            addActionListener { welcomeCtrl.preferences_configureDeliveryDestTemplate_onClickCancel() }
        }

        fun configureDeliveryDestationDoneOrApplyCallback(done: Boolean) {
            welcomeCtrl.preferences_configureDeliveryDestTemplate_onClickDoneOrApply(
                done,
                configureDeliveryDestTemplateForm.nameWidget.value,
                configureDeliveryDestTemplateForm.templateStrWidget.value
            )
        }
        configureDeliveryDestTemplateDoneButton = JButton(l10n("ok"), CHECK_ICON).apply {
            addActionListener { configureDeliveryDestationDoneOrApplyCallback(true) }
        }
        configureDeliveryDestTemplateApplyButton = JButton(l10n("apply"), CHECK_ICON).apply {
            addActionListener { configureDeliveryDestationDoneOrApplyCallback(false) }
        }
        configureDeliveryDestTemplateForm.onChange(configureDeliveryDestTemplateForm.nameWidget)  // Run validation
        val configureDeliverLocationTemplatePanel = JPanel(MigLayout("insets 20, wrap")).apply {
            background = null
            add(newLabelTextArea(l10n("ui.preferences.deliveryDestTemplates.configure.prompt")), "growx")
            add(configureDeliveryDestTemplateForm, "grow, push, gaptop para")
            add(configureDeliveryDestTemplateCancelButton, "split 3, right")
            add(configureDeliveryDestTemplateDoneButton)
            add(configureDeliveryDestTemplateApplyButton)
        }

        add(startPanel, PreferencesCard.START.name)
        add(configureAccountPanel, PreferencesCard.CONFIGURE_ACCOUNT.name)
        add(establishAccountPanel, PreferencesCard.ESTABLISH_ACCOUNT.name)
        add(configureOverlayPanel, PreferencesCard.CONFIGURE_OVERLAY.name)
        add(configureDeliverLocationTemplatePanel, PreferencesCard.CONFIGURE_DELIVERY_LOC_TEMPLATE.name)

        @Suppress("DEPRECATION")
        leakedStartAddAccountButton = startAddAccountButton
        @Suppress("DEPRECATION")
        leakedStartAddOverlayButton = startAddOverlayButton
    }


    /* ***************************
       ********** COMMS **********
       *************************** */

    fun preferences_setCard(card: PreferencesCard) {
        cards.show(this, card.name)
    }

    fun preferences_start_setInitialSetup(initialSetup: Boolean, doneListener: (() -> Unit)?) {
        require(initialSetup == (doneListener != null))
        startLowerPanel.isVisible = !initialSetup
        startPreferencesForm.setPreferencesSubmitButton(doneListener)
    }

    fun preferences_start_setAccounts(accounts: List<Account>) {
        startAccountsPanel.removeAll()
        startAccountsRemovalButtons.keys.retainAll(accounts)
        for (account in accounts) {
            val removeButton = JButton(TRASH_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickRemoveAccount(account) }
            }
            val accountPanel = JPanel(MigLayout("", "[]push[]")).apply {
                border = FlatRoundBorder()
                add(JLabel(account.service.product, GLOBE_ICON, JLabel.LEADING), "split 2, flowy")
                add(JLabel(account.id, LABEL_ICON, JLabel.LEADING).apply { toolTipText = account.id }, "wmax 230")
                add(removeButton)
            }
            startAccountsPanel.add(accountPanel)
            startAccountsRemovalButtons.merge(account, removeButton) { o, n -> n.apply { isEnabled = o.isEnabled } }
        }
        startAccountsPanel.isVisible = accounts.isNotEmpty()
        // Without this, when there are two accounts and the user removes one, an afterimage of the removed one remains.
        startAccountsPanel.repaint()
    }

    fun preferences_start_setAccountRemovalLocked(account: Account, locked: Boolean) {
        startAccountsRemovalButtons[account]?.isEnabled = !locked
    }

    fun preferences_start_setOverlays(overlays: List<ConfigurableOverlay>) {
        startOverlaysPanel.removeAll()
        for (overlay in overlays) {
            val editButton = JButton(EDIT_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickEditOverlay(overlay) }
            }
            val removeButton = JButton(TRASH_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickRemoveOverlay(overlay) }
            }
            val overlayPanel = JPanel(MigLayout("", "[]push[][]")).apply {
                border = FlatRoundBorder()
                add(JLabel(overlay.label, overlay.icon, JLabel.LEADING).apply {
                    toolTipText = overlay.label
                }, "wmax 230")
                add(editButton)
                add(removeButton)
            }
            startOverlaysPanel.add(overlayPanel)
        }
        startOverlaysPanel.isVisible = overlays.isNotEmpty()
        // Without this, when there are two overlays and the user removes one, an afterimage of the removed one remains.
        startOverlaysPanel.repaint()
    }

    fun preferences_start_setDeliveryDestTemplates(templates: List<DeliveryDestTemplate>) {
        startDeliveryDestTemplatesPanel.removeAll()
        for (template in templates) {
            val editButton = JButton(EDIT_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickEditDeliveryDestTemplate(template) }
            }
            val removeButton = JButton(TRASH_ICON).apply {
                addActionListener { welcomeCtrl.preferences_start_onClickRemoveDeliveryDestTemplate(template) }
            }
            val templatePanel = JPanel(MigLayout("", "[]push[][]")).apply {
                border = FlatRoundBorder()
                add(JLabel(template.name, LABEL_ICON, JLabel.LEADING).apply {
                    toolTipText = text
                }, "split 2, flowy, wmax 520")
                add(JLabel(template.l10nStr(), TEMPLATE_ICON, JLabel.LEADING).apply {
                    toolTipText = text
                }, "wmax 520")
                add(editButton)
                add(removeButton)
            }
            startDeliveryDestTemplatesPanel.add(templatePanel)
        }
        startDeliveryDestTemplatesPanel.isVisible = templates.isNotEmpty()
    }

    fun preferences_configureAccount_resetForm() {
        configureAccountForm.apply {
            labelWidget.value = ""
            serviceWidget.value = Optional.empty()
            serverWidget.value = "https://"
        }
    }

    fun preferences_establishAccount_setAction(authorize: Boolean) {
        if (authorize) {
            establishAccountMsgTextArea.text = l10n("ui.preferences.accounts.authorize.msg")
            establishAccountErrorTextArea.text = l10n("ui.preferences.accounts.authorize.error")
        } else {
            establishAccountMsgTextArea.text = l10n("ui.preferences.accounts.validate.msg")
            establishAccountErrorTextArea.text = l10n("ui.preferences.accounts.validate.error")
        }
    }

    fun preferences_establishAccount_setError(error: String?) {
        val hasError = error != null
        establishAccountErrorTextArea.isVisible = hasError
        establishAccountResponseTextArea.isVisible = hasError
        establishAccountResponseTextArea.text = error ?: ""
    }

    fun preferences_configureOverlay_setForm(
        type: Class<out ConfigurableOverlay>,
        name: String,
        aspectRatioH: Double,
        aspectRatioV: Double,
        linesColor: Color4f?,
        linesH: List<Int>,
        linesV: List<Int>,
        imageFile: Path,
        imageUnderlay: Boolean
    ) {
        configureOverlayForm.apply {
            typeWidget.value = type
            nameWidget.value = name
            aspectRatioHWidget.value = aspectRatioH
            aspectRatioVWidget.value = aspectRatioV
            linesColorWidget.value = Opt(linesColor != null, linesColor ?: OVERLAY_COLOR)
            linesHWidget.value = linesH
            linesVWidget.value = linesV
            imageFileWidget.value = imageFile
            imageUnderlayWidget.value = imageUnderlay
        }
    }

    fun preferences_configureOverlay_clearImageFile() {
        configureOverlayForm.imageFileWidget.value = Path("")
    }

    fun preferences_configureOverlay_setImageFileExtAssortment(fileExtAssortment: FileExtAssortment?) {
        configureOverlayForm.imageFileWidget.fileExtAssortment = fileExtAssortment
    }

    fun preferences_configureDeliveryDestTemplate_setForm(name: String, templateStr: String) {
        configureDeliveryDestTemplateForm.apply {
            nameWidget.value = name
            templateStrWidget.value = templateStr
        }
    }


    private inner class ConfigureAccountForm : EasyForm(insets = false, noticeArea = true, constLabelWidth = false) {

        val labelWidget = addWidget(
            l10n("ui.preferences.accounts.configure.label"),
            TextWidget(),
            verify = { label ->
                welcomeCtrl.preferences_configureAccount_verifyLabel(label)?.let { Notice(Severity.ERROR, it) }
            }
        )

        val serviceWidget = addWidget(
            l10n("ui.preferences.accounts.configure.service"),
            OptionalComboBoxWidget(
                Service::class.java, SERVICES, widthSpec = WidthSpec.WIDE,
                toString = Service::product
            ).apply { value = Optional.empty() },
            verify = { optional ->
                if (optional.isPresent) null else
                    Notice(Severity.ERROR, l10n("ui.preferences.accounts.configure.noServiceSelected"))
            }
        )

        val serverWidget = addWidget(
            l10n("ui.preferences.accounts.configure.server"),
            TextWidget(),
            isVisible = { serviceWidget.value.getOrNull()?.accountNeedsServer ?: false },
            verify = { server ->
                welcomeCtrl.preferences_configureAccount_verifyServer(serviceWidget.value.getOrNull(), server)
                    ?.let { Notice(Severity.ERROR, it) }
            }
        )

        public override fun onChange(widget: Widget<*>) {
            super.onChange(widget)
            val service = serviceWidget.value.getOrNull()
            val establishText = service?.authorizer?.let { l10n("ui.preferences.accounts.configure.authorize", it) }
                ?: l10n("ui.preferences.accounts.add")
            val establishIcon = service?.let {
                val icon = service.icon
                object : FlatAbstractIcon(36, 36, null) {
                    override fun paintIcon(c: Component, g2: Graphics2D) {
                        g2.color = Color.WHITE
                        g2.fillRoundRect(0, 0, iconWidth, iconHeight, 4, 4)
                        icon.paintIcon(c, g2, (iconWidth - icon.iconWidth) / 2, (iconHeight - icon.iconHeight) / 2)
                    }
                }
            }
            configureAccountEstablishButton.apply {
                isVisible = isErrorFree
                text = establishText
                icon = establishIcon
            }
        }

    }


    private inner class ConfigureOverlayForm : EasyForm(insets = false, noticeArea = true, constLabelWidth = false) {

        val typeWidget = addWidget(
            l10n("ui.preferences.overlays.configure.type"),
            ToggleButtonGroupWidget(
                ConfigurableOverlay.TYPES,
                toIcon = Overlay::icon,
                toLabel = ConfigurableOverlay::typeName
            )
        )

        val nameWidget = addWidget(
            l10n("ui.preferences.overlays.configure.name"),
            TextWidget(),
            isVisible = { val t = typeWidget.value; t == LinesOverlay::class.java || t == ImageOverlay::class.java },
            verify = { name ->
                welcomeCtrl.preferences_configureOverlay_verifyName(name)?.let { Notice(Severity.ERROR, it) }
            }
        )

        val aspectRatioHWidget = makeAspectRatioSpinnerWidget()
        val aspectRatioVWidget = makeAspectRatioSpinnerWidget()

        init {
            addWidget(
                l10n("ui.overlays.type.aspectRatio"),
                UnionWidget(
                    listOf(aspectRatioHWidget, aspectRatioVWidget),
                    labels = listOf(null, ":"), gaps = listOf("rel")
                ),
                isVisible = { typeWidget.value == AspectRatioOverlay::class.java }
            )
        }

        val linesColorWidget = addWidget(
            l10n("color"),
            OptWidget(ColorWellWidget(allowNonSRGB = false, allowAlpha = false)),
            isVisible = { typeWidget.value == LinesOverlay::class.java }
        )

        val linesHWidget = addWidget(
            l10n("ui.preferences.overlays.configure.linesH") + " [px]",
            SimpleListWidget(makeElementWidget = ::makeLineSpinnerWidget, newElement = 0, elementsPerRow = 2),
            description = l10n("ui.preferences.overlays.configure.lines.desc"),
            isVisible = { typeWidget.value == LinesOverlay::class.java }
        )

        val linesVWidget = addWidget(
            l10n("ui.preferences.overlays.configure.linesV") + " [px]",
            SimpleListWidget(makeElementWidget = ::makeLineSpinnerWidget, newElement = 0, elementsPerRow = 2),
            description = l10n("ui.preferences.overlays.configure.lines.desc"),
            isVisible = { typeWidget.value == LinesOverlay::class.java }
        )

        val imageFileWidget = addWidget(
            l10n("ui.overlays.type.image"),
            FileWidget(FileType.FILE, FileAction.OPEN),
            isVisible = { typeWidget.value == ImageOverlay::class.java }
        )

        val imageUnderlayWidget = addWidget(
            l10n("ui.preferences.overlays.configure.imageLayer"),
            ToggleButtonGroupWidget(
                listOf(true, false),
                toLabel = {
                    when (it) {
                        true -> l10n("ui.preferences.overlays.configure.imageLayer.under")
                        false -> l10n("ui.preferences.overlays.configure.imageLayer.over")
                    }
                }
            ),
            isVisible = { typeWidget.value == ImageOverlay::class.java }
        )

        private fun makeAspectRatioSpinnerWidget() = SpinnerWidget(
            Double::class.javaObjectType, SpinnerNumberModel(1.0, 1.0, null, 1.0), widthSpec = WidthSpec.LITTLE
        )

        private fun makeLineSpinnerWidget() = SpinnerWidget(
            Int::class.javaObjectType, SpinnerNumberModel(0, null, null, 1), widthSpec = WidthSpec.LITTLE
        )

        override fun onChange(widget: Widget<*>) {
            super.onChange(widget)
            val fine = isErrorFree
            configureOverlayDoneButton.isEnabled = fine
            configureOverlayApplyButton.isEnabled = fine
        }

    }


    private inner class ConfigureDeliveryDestTemplateForm :
        EasyForm(insets = false, noticeArea = true, constLabelWidth = false) {

        val nameWidget = addWidget(
            l10n("ui.preferences.deliveryDestTemplates.configure.name"),
            TextWidget(),
            verify = { name ->
                welcomeCtrl.preferences_configureDeliveryDestTemplate_verifyName(name)
                    ?.let { Notice(Severity.ERROR, it) }
            }
        )

        val templateStrWidget = addWidget(
            l10n("template"),
            TextModulesWidget(DeliveryDestTemplate.Placeholder.entries.map { it.l10nTagBraces() }, WidthSpec.FILL),
            verify = { templateStr ->
                welcomeCtrl.preferences_configureDeliveryDestTemplate_verifyTemplateStr(templateStr)
                    ?.let { Notice(Severity.ERROR, it) }
            }
        )

        public override fun onChange(widget: Widget<*>) {
            super.onChange(widget)
            val fine = isErrorFree
            configureDeliveryDestTemplateDoneButton.isEnabled = fine
            configureDeliveryDestTemplateApplyButton.isEnabled = fine
        }

    }

}
