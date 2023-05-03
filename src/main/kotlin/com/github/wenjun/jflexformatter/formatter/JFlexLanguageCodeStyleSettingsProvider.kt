package com.github.wenjun.jflexformatter.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.WrappingOrBraceOption
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.intellij.jflex.JFlexLanguage

class JFlexLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language = JFlexLanguage.INSTANCE

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    override fun getCodeSample(settingsType: SettingsType): String = Sample

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions(
                    WrappingOrBraceOption.RIGHT_MARGIN.name,
                    WrappingOrBraceOption.WRAP_ON_TYPING.name,
                    WrappingOrBraceOption.KEEP_LINE_BREAKS.name,
                    WrappingOrBraceOption.WRAP_LONG_LINES.name,
                )
                consumer.showCustomOption(
                    JFlexCodeStyleSettings::class.java,
                    "KEEP_TRAILING_COMMA",
                        JFlexBundle.message("formatter.trailing_comma.label"),
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAPPING_KEEP
                )

            }

            else -> {

            }
        }
    }

    companion object {
        val Sample = """
            |import com.intellij.lexer.FlexLexer;
            |import com.intellij.psi.tree.IElementType;
            |import com.intellij.psi.TokenType;import groovyjarjarantlr.Token;
            |
            |%%
            |%class Lexer
            |%implements FlexLexer
            |%function advance
            |%type IElementType
            |%unicode
            |%line
            |%column
            |
            |%{
            |  private StringBuffer string = new StringBuffer();
            |  private int yyline = 0;
            |  private int yycolumn = 0;
            |
            |  private Symbol symbol(IElementType type){
            |      return new Symbol(type, yyline, yycolumn);
            |  }
            |
            |  private Symbol symbol(IElementType type,Object value) {
            |      return new Symbol(type, yyline, yycolumn, value);
            |  }
            |%}
            |LineTerminator = \r|\n|\r\n
            |InputCharacter = [^\r\n]
            |WhiteSpace = {LineTerminator}|[ \t\f]
            |
            |Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
            |TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
            |EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
            |DocumentationComment  ="/**" {CommentContent} "*"+ "/"
            |CommentContent = ([^*]|\*+ [^/*])*
            |Identifier = [:jletter:][:jletterdigit:]*
            |DecIntegerLiteral = 0 | [1-9][0-9]*
            |%state STRING
            |%%
            |
            |<YYINITIAL> "abstract" {return Tokens.INSTANCE.ABSTRACT;}
            |<YYINITIAL> "boolean" {return Tokens.INSTANCE.BOOLEAN;}
            |<YYINITIAL> "break" {return Tokens.INSTANCE.BREAK;}
            |<YYINITIAL> {
            |/* identifiers */
            |{Identifier} {return Tokens.INSTANCE.IDENTIFIER;}
            |{DecIntegerLiteral} {return Tokens.INSTANCE.INTEGER_LITERAL;}
            | \" {string.setLength(0);yybegin(STRING);}
            | = {return Tokens.INSTANCE.EQUAL;}
            |}
        """.trimMargin()
    }
}