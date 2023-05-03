package com.github.wenjun.jflexformatter.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.TokenSet
import org.intellij.jflex.JFlexLanguage
import org.intellij.jflex.psi.JFlexTypes

class JFlexFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        val spaceBuilder = createSpaceBuilder(codeStyleSettings)
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.containingFile,
                JFlexBlock(
                        formattingContext.node,
                        null,
                        null,
                        null,
                        spaceBuilder
                ),
                codeStyleSettings
        )
    }

    companion object {
        fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
            val cs = settings.getCommonSettings(JFlexLanguage.INSTANCE.id)
            val exprs = TokenSet.create(JFlexTypes.FLEX_CHOICE_EXPRESSION,
                    JFlexTypes.FLEX_MACRO_REF_EXPRESSION,
                    JFlexTypes.FLEX_LITERAL_EXPRESSION)
            return SpacingBuilder(settings, JFlexLanguage.INSTANCE)
                    .around(JFlexTypes.FLEX_EQ)
                    .spaceIf(cs.SPACE_AROUND_EQUALITY_OPERATORS)
                    .after(JFlexTypes.FLEX_COMMA)
                    .spaceIf(cs.SPACE_AFTER_COMMA)
                    .between(JFlexTypes.FLEX_STATE_LIST, exprs)
                    .spacing(1, 1, 0, false, 0)
                    .between(exprs, JFlexTypes.FLEX_JAVA_CODE)
                    .spacing(1, 1, 0, false, 0)

        }
    }
}