package de.nordgedanken.auto_hotkey.runconfig.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import de.nordgedanken.auto_hotkey.project.configurable.AhkProjectConfigurable
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfig
import de.nordgedanken.auto_hotkey.runconfig.model.AhkSwitch
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkConstants
import javax.swing.JComponent

/**
 * Contains the settings that are shown when editing a run configuration.
 *
 * (If you're new to Kotlin, createEditor() will seem weird but it's just using
 * the UI DSL format to construct the UI instead of a .form file)
 */
class AhkRunConfigSettingsEditor(private val project: Project) : SettingsEditor<AhkRunConfig>() {
    private val pathToScriptTextField = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            AhkBundle.msg("runconfig.configtab.scriptpath.filechooser.title"),
            AhkBundle.msg("runconfig.configtab.scriptpath.filechooser.message"),
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(AhkConstants.FILE_EXTENSION)
        )
    }
    private val argumentsTextField = ExpandableTextField()
    private val ahkSdkComboBox = AhkSdkComboBox(project)
    private val openProjectSettingsButton = FixedSizeButton().apply {
        icon = AllIcons.General.GearPlain
        toolTipText = AhkBundle.msg("runconfig.configtab.scriptrunner.projectsettingsbutton.tooltip")
        addActionListener { openProjSettingsAndThenTriggerEditorUpdate() }
    }
    private val printErrToConsoleCheckBox = JBCheckBox(
        AhkBundle.msg("runconfig.configtab.switches.errorstdout.label"), true
    ).apply {
        toolTipText = AhkBundle.msg("runconfig.configtab.switches.errorstdout.tooltip")
    }

    override fun resetEditorFrom(s: AhkRunConfig) {
        pathToScriptTextField.text = s.runConfigSettings.pathToScript
        argumentsTextField.text = s.runConfigSettings.arguments
        ahkSdkComboBox.setSelectedSdkByName(s.runConfigSettings.runner)
        printErrToConsoleCheckBox.isSelected = s.runConfigSettings.switches.getOrDefault(AhkSwitch.ERROR_STD_OUT, true)
    }

    override fun applyEditorTo(s: AhkRunConfig) {
        s.runConfigSettings.pathToScript = pathToScriptTextField.text
        s.runConfigSettings.arguments = argumentsTextField.text
        s.runConfigSettings.runner = ahkSdkComboBox.getSelectedSdkName()
        s.runConfigSettings.switches[AhkSwitch.ERROR_STD_OUT] = printErrToConsoleCheckBox.isSelected
    }

    override fun createEditor(): JComponent = panel {
        row {
            tabbedPane(CCFlags.growX, CCFlags.pushX) {
                outlinedTab(AhkBundle.msg("runconfig.configtab.label")) {
                    row(AhkBundle.msg("runconfig.configtab.scriptpath.label")) { pathToScriptTextField() }
                    row(AhkBundle.msg("runconfig.configtab.scriptargs.label")) { argumentsTextField(growX, pushX) }
                    row(AhkBundle.msg("runconfig.configtab.scriptrunner.label")) {
                        ahkSdkComboBox(growX, pushX)
                        openProjectSettingsButton()
                    }
                    noteRow(AhkBundle.msg("runconfig.general.info.label"))
                    titledRow("Additional Options") {
                        row { printErrToConsoleCheckBox() }
                    }
                }
            }
        }
    }

    /**
     * Executed when the openProjectSettingsButton is clicked. The extra logic
     * here is needed when you make a change to the sdk that is already selected
     * in the dropdown so that the editor state is updated (otherwise the editor
     * sees that the selected sdk is the same and does not fire any update events)
     */
    private fun openProjSettingsAndThenTriggerEditorUpdate() {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, AhkProjectConfigurable::class.java)
        ahkSdkComboBox.updateSdkList()
        // this part is just to trigger the settings editor event listeners to update the error message
        val tmp = ahkSdkComboBox.selectedItem
        ahkSdkComboBox.selectedItem = null
        fireEditorStateChanged()
        ahkSdkComboBox.selectedItem = tmp
    }
}
