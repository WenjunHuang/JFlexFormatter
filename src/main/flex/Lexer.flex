package com.example.demo1;

import com.github.wenjun.jflexformatter.demo1.Tokens;import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;import groovyjarjarantlr.Token;

%%
%class Lexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%line
%column

%{
  private StringBuffer string = new StringBuffer();
  private int yyline = 0;
  private int yycolumn = 0;

  private Symbol symbol(IElementType type){
      return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(IElementType type,Object value) {
      return new Symbol(type, yyline, yycolumn, value);
  }
%}
LineTerminator =\r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator}|[ \t\f]

Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment  ="/**" {CommentContent} "*"+ "/"
CommentContent = ([^*]|\*+ [^/*])*
Identifier = [:jletter:][:jletterdigit:]*
DecIntegerLiteral = 0 | [1-9][0-9]*
%state STRING
%%

<YYINITIAL,STRING>  "abstract" {return Tokens.INSTANCE.ABSTRACT;}

<YYINITIAL> "boolean" {return Tokens.INSTANCE.BOOLEAN;}
<YYINITIAL> "break"|{Comment} {return Tokens.INSTANCE.BREAK;}
<YYINITIAL> {
/* identifiers */
{Identifier} {return Tokens.INSTANCE.IDENTIFIER;}
{DecIntegerLiteral} {return Tokens.INSTANCE.INTEGER_LITERAL;}
 \" {string.setLength(0);yybegin(STRING);}
 = {return Tokens.INSTANCE.EQUAL;}
}


