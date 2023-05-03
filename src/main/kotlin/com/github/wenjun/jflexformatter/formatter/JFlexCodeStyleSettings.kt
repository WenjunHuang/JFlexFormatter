package com.github.wenjun.jflexformatter.formatter

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import org.intellij.jflex.JFlexLanguage

class JFlexCodeStyleSettings(container: CodeStyleSettings) :
    CustomCodeStyleSettings(JFlexLanguage.INSTANCE.id, container) {
    @JvmField
    var KEEP_TRAILING_COMMA = false
}