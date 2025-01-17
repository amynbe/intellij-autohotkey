package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons
import javax.swing.Icon

class AhkColorSettingsPage : ColorSettingsPage {
    private val ATTRS = AhkHighlighterColor.values().map { it.attributesDescriptor }.toTypedArray()
    private val ANNOTATOR_TAGS = AhkHighlighterColor.values().associateBy({ it.name }, { it.textAttributesKey })
    private val DEMO_TEXT by lazy {
        val stream = javaClass.getResourceAsStream("demo_text_for_color_settings_page.ahk")!!
        StreamUtil.convertSeparators(StreamUtil.readText(stream, "UTF-8"))
    }

    override fun getDisplayName() = AhkConstants.LANGUAGE_NAME
    override fun getDemoText() = DEMO_TEXT
    override fun getIcon(): Icon = AhkIcons.LOGO
    override fun getAttributeDescriptors() = ATTRS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getHighlighter() = AhkSyntaxHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap() = ANNOTATOR_TAGS
}
