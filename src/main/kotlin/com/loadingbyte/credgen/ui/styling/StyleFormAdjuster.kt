package com.loadingbyte.credgen.ui.styling

import com.loadingbyte.credgen.imaging.Color4f
import com.loadingbyte.credgen.imaging.Picture
import com.loadingbyte.credgen.imaging.Tape
import com.loadingbyte.credgen.project.*
import com.loadingbyte.credgen.ui.helper.FontFamilies
import com.loadingbyte.credgen.ui.helper.Form
import java.util.*
import javax.swing.Icon


class StyleFormAdjuster(
    private val forms: List<StyleForm<*>>,
    private val getCurrentStyling: () -> Styling?,
    private val getCurrentStyleInActiveForm: () -> Style?,
    private val notifyConstraintViolations: (List<ConstraintViolation>) -> Unit,
    // ========== ENCAPSULATION LEAKS ==========
    @Suppress("DEPRECATION")
    private val styleIdxAndSiblingsOverride: StyleIdxAndSiblingsOverride? = null
    // =========================================
) {

    // ========== ENCAPSULATION LEAKS ==========
    @Deprecated("ENCAPSULATION LEAK")
    interface StyleIdxAndSiblingsOverride {
        fun <S : Style> getStyleIdxAndSiblings(style: S): Pair<Int, List<S>>
    }
    // =========================================

    // Cache the current Styling's constraint violations and all colors used in the current Styling.
    private var constraintViolations: List<ConstraintViolation> = emptyList()
    private var swatchColors: List<Color4f> = emptyList()

    var activeForm: StyleForm<*>? = null
        set(activeForm) {
            require(activeForm == null || activeForm in forms)
            field = activeForm
        }

    fun onLoadStyling() {
        refreshConstraintViolations()
        refreshSwatchColors()
    }

    fun onLoadStyleIntoActiveForm() {
        adjustActiveForm()
    }

    fun onChangeInActiveForm() {
        refreshConstraintViolations()
        refreshSwatchColors()
        adjustActiveForm()
    }

    fun updateProjectFontFamilies(projectFamilies: FontFamilies) {
        for (form in forms)
            form.setProjectFontFamilies(projectFamilies)
    }

    fun updatePictureLoaders(pictureLoaders: Collection<Picture.Loader>) {
        updateExternalChoices(PictureRef::class.java, pictureLoaders.map(::PictureRef))
    }

    fun updateTapes(tapes: Collection<Tape>) {
        updateExternalChoices(TapeRef::class.java, tapes.map(::TapeRef))
    }

    private fun <V : Any> updateExternalChoices(settingType: Class<V>, choices: List<V>) {
        for (form in forms)
            for (setting in getStyleSettings(form.styleClass))
                if (setting.type == settingType)
                    form.setChoices(setting, choices)
    }

    private fun refreshConstraintViolations() {
        constraintViolations = verifyConstraints(getCurrentStyling() ?: return)
        notifyConstraintViolations(constraintViolations)
    }

    private fun refreshSwatchColors() {
        val styling = getCurrentStyling() ?: return

        val colorSet = HashSet<Color4f>()
        colorSet.add(styling.global.grounding)
        for (letterStyle in styling.letterStyles)
            for (layer in letterStyle.layers) {
                if (layer.coloring != LayerColoring.OFF)
                    colorSet.add(layer.color1)
                if (layer.coloring == LayerColoring.GRADIENT)
                    colorSet.add(layer.color2)
            }

        swatchColors = colorSet.toList().sortedWith { c1, c2 -> Arrays.compare(c1.toHSB(), c2.toHSB()) }
    }

    private fun adjustActiveForm() {
        val activeForm = this.activeForm ?: return
        val curStyle = getCurrentStyleInActiveForm() ?: return
        adjustForm(activeForm.castToStyle(curStyle.javaClass), curStyle)
    }

    private fun <S : Style> adjustForm(
        curForm: StyleForm<S>, curStyle: S, curStyleIdx: Int = 0, siblingStyles: List<S> = emptyList()
    ) {
        val styling = getCurrentStyling() ?: return

        // Support for the leaking override.
        @Suppress("NAME_SHADOWING") var curStyleIdx = curStyleIdx
        @Suppress("NAME_SHADOWING") var siblingStyles = siblingStyles
        if (styleIdxAndSiblingsOverride != null) {
            val pair = styleIdxAndSiblingsOverride.getStyleIdxAndSiblings(curStyle)
            curStyleIdx = pair.first
            siblingStyles = pair.second
        }

        curForm.ineffectiveSettings = findIneffectiveSettings(styling, curStyle)

        curForm.clearIssues()
        for (violation in constraintViolations)
            if (violation.leafStyle == curStyle) {
                val issue = Form.Notice(violation.severity, violation.msg)
                curForm.showIssueIfMoreSevere(violation.leafSetting, violation.leafSubjectIndex, issue)
            }

        curForm.setSwatchColors(swatchColors)

        for (constr in getStyleConstraints(curStyle.javaClass)) when (constr) {
            is DynChoiceConstr<S, *> -> {
                val choices = constr.choices(styling, curStyle).toList()
                for (setting in constr.settings)
                    curForm.setChoices(setting, choices)
            }
            is StyleNameConstr<S, *> -> {
                val choiceSet = constr.choices(styling, curStyle).mapTo(TreeSet(), ListedStyle::name)
                if (constr.clustering)
                    choiceSet.remove((curStyle as ListedStyle).name)
                val choices = choiceSet.toList()
                for (setting in constr.settings)
                    curForm.setChoices(setting, choices)
            }
            is FontFeatureConstr -> {
                val availableTags = constr.getAvailableTags(styling, curStyle).toList()
                for (setting in constr.settings)
                    curForm.setChoices(setting, availableTags, unique = true)
            }
            is TapeSliceConstr -> {
                val fps = constr.getFPS(styling, curStyle)
                val timecodeFormats = constr.getTimecodeFormats(styling, curStyle)
                val range = constr.getRange(styling, curStyle)
                for (setting in constr.settings)
                    curForm.setTapeSliceContext(setting, fps, timecodeFormats, range)
            }
            is DynSizeConstr -> {
                val size = constr.size(styling, curStyle)
                for (setting in constr.settings)
                    curForm.setListSize(setting, size)
            }
            is SiblingOrdinalConstr -> {
                val choices = LinkedHashMap<Int, String>()  // retains insertion order
                for ((idx, sibling) in siblingStyles.withIndex())
                    if (constr.permitSibling(styling, curStyle, curStyleIdx + 1, sibling, idx + 1))
                        choices[idx + 1] = if (sibling is NamedStyle) sibling.name else ""
                val toString = SiblingOrdinalToString(choices)
                for (setting in constr.settings) {
                    curForm.setChoices(setting, choices.keys.toList())
                    curForm.setToStringFun(setting, toString)
                }
            }
            else -> {}
        }

        for (spec in getStyleWidgetSpecs(curStyle.javaClass)) when (spec) {
            is ToggleButtonGroupWidgetSpec<S, *> -> {
                fun <SUBJ : Any> makeToIcon(spec: ToggleButtonGroupWidgetSpec<S, SUBJ>): ((SUBJ) -> Icon)? =
                    spec.getDynIcon?.let { return fun(item: SUBJ) = it(styling, curStyle, item) }

                val toIcon = makeToIcon(spec)
                if (toIcon != null)
                    for (setting in spec.settings)
                        curForm.setToIconFun(setting, toIcon)
            }
            is MultiplierWidgetSpec -> {
                val multiplier = spec.getMultiplier(styling, curStyle)
                for (setting in spec.settings)
                    curForm.setMultiplier(setting, multiplier)
            }
            is TimecodeWidgetSpec -> {
                val fps = spec.getFPS(styling, curStyle)
                val timecodeFormat = spec.getTimecodeFormat(styling, curStyle)
                for (setting in spec.settings)
                    curForm.setTimecodeFPSAndFormat(setting, fps, timecodeFormat)
            }
            else -> {}
        }

        val (nestedForms, nestedStyles) = curForm.getNestedFormsAndStyles(curStyle)
        for (idx in nestedForms.indices)
            adjustForm(nestedForms[idx].castToStyle(nestedStyles[idx].javaClass), nestedStyles[idx], idx, nestedStyles)
    }


    // This toString function is also a data class so that when a new instance is pushed into a widget, the widget can
    // avoid updating itself in the common case where nothing has changed.
    private data class SiblingOrdinalToString(val choices: Map<Int, String>) : (Int) -> String {
        override fun invoke(choice: Int): String {
            val choiceName = choices.getOrDefault(choice, "")
            return if (choiceName.isBlank()) "$choice" else "$choice $choiceName"
        }
    }

}
