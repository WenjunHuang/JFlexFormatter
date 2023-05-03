package com.github.wenjun.jflexformatter.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import org.intellij.jflex.JFlexLanguage

class JFlexCodeStyleSettingsProvider: CodeStyleSettingsProvider() {
    override fun createConfigurable(
        settings: CodeStyleSettings,
        modelSettings: CodeStyleSettings
    ): CodeStyleConfigurable {
        return object:CodeStyleAbstractConfigurable(settings,modelSettings,"JFlex"){
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return object:TabbedLanguageCodeStylePanel(JFlexLanguage.INSTANCE, currentSettings,settings){
                    override fun initTabs(settings: CodeStyleSettings?) {
                        addIndentOptionsTab(settings)
//                        addSpacesTab(settings)
//                        addBlankLinesTab(settings)
                        addWrappingAndBracesTab(settings)
                    }
                }
            }
        }
    }

    override fun getConfigurableDisplayName(): String = JFlexLanguage.INSTANCE.displayName

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings? {
        return JFlexCodeStyleSettings(settings)
    }
}