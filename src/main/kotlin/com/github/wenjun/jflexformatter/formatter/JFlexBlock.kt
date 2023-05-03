package com.github.wenjun.jflexformatter.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import org.intellij.jflex.psi.JFlexRule
import org.intellij.jflex.psi.JFlexTypes

class JFlexBlock(
        node: ASTNode,
        wrap: Wrap?,
        alignment: Alignment?,
        private val indent: Indent?,
        private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {
    private val psiElement = node.psi
    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean =
            myNode.firstChildNode == null

    override fun buildChildren(): List<Block> {
        val children = myNode.getChildren(null)
        return children.filterNot {
            it.elementType == TokenType.WHITE_SPACE || it.text.isBlank()
        }.map { makeSubBlock(it) }
    }

    override fun getIndent(): Indent? = indent

    override fun getChildIndent(): Indent =
            when (psiElement.elementType) {
//                JFlexTypes.FLEX_DECLARATIONS_SECTION -> Indent.getNormalIndent()
//                JFlexTypes.FLEX_LEXICAL_RULES_SECTION -> Indent.getNormalIndent()
                JFlexTypes.FLEX_RULE ->
                    if (psiElement.parent.elementType == JFlexTypes.FLEX_RULE) Indent.getNormalIndent()
                    else Indent.getNoneIndent()
//                JFlexTypes.FLEX_JAVA_CODE -> Indent.getNormalIndent()
//                JFlexTypes.FLEX_MACRO_DEFINITION -> Indent.getNormalIndent()
//                JFlexTypes.FLEX_EQ -> Indent.getNormalIndent()
//                JFlexTypes.FLEX_STATE_LIST -> Indent.getNormalIndent()
                else -> Indent.getNoneIndent()
            }

    private fun makeSubBlock(childNode: ASTNode): Block {
        var alignment: Alignment? = null
        var wrap: Wrap? = null
        when (childNode.elementType) {
            JFlexTypes.FLEX_DECLARATIONS_SECTION -> {
                childNode.psi.putUserData(AlignKey, Alignment.createAlignment(true, Alignment.Anchor.LEFT))
            }

            JFlexTypes.FLEX_RULE -> {
                if (psiElement.elementType == JFlexTypes.FLEX_LEXICAL_RULES_SECTION) {
                    PsiTreeUtil.findChildOfType(childNode.psi, JFlexRule::class.java)?.let {
                        // 嵌套的rule
                        childNode.psi.putUserData(LexicalRuleExprAlignKey, Alignment.createAlignment(true, Alignment.Anchor.LEFT))
                        childNode.psi.putUserData(LexicalRuleJavaBlockAlignKey, Alignment.createAlignment(true, Alignment.Anchor.LEFT))
                    }
                }
            }

            JFlexTypes.FLEX_EQ -> {
                if (myNode.elementType == JFlexTypes.FLEX_MACRO_DEFINITION) {
                    PsiTreeUtil.findFirstParent(psiElement) { it.elementType == JFlexTypes.FLEX_DECLARATIONS_SECTION }?.let {
                        alignment = it.getUserData(AlignKey)
                    }
                }
            }

            JFlexTypes.FLEX_LEXICAL_RULES_SECTION -> {
                childNode.psi.putUserData(LexicalRuleExprAlignKey, Alignment.createAlignment(true, Alignment.Anchor.LEFT))
                childNode.psi.putUserData(LexicalRuleJavaBlockAlignKey, Alignment.createAlignment(true, Alignment.Anchor.LEFT))
            }

            JFlexTypes.FLEX_MACRO_REF_EXPRESSION,
            JFlexTypes.FLEX_CHOICE_EXPRESSION,
            JFlexTypes.FLEX_LITERAL_EXPRESSION -> {
                if (psiElement.elementType == JFlexTypes.FLEX_RULE && psiElement.parent.elementType == JFlexTypes.FLEX_RULE) {
                    psiElement.parent.getUserData(LexicalRuleExprAlignKey)?.let {
                        alignment = it
                    }
                } else {
                    PsiTreeUtil.findFirstParent(psiElement) { it.elementType == JFlexTypes.FLEX_LEXICAL_RULES_SECTION }?.let {
                        alignment = it.getUserData(LexicalRuleExprAlignKey)
                    }
                }
            }

            JFlexTypes.FLEX_JAVA_CODE -> {
                if (psiElement.elementType == JFlexTypes.FLEX_RULE && psiElement.parent.elementType == JFlexTypes.FLEX_RULE) {
                    psiElement.parent.getUserData(LexicalRuleJavaBlockAlignKey)?.let {
                        alignment = it
                    }
                } else {
                    PsiTreeUtil.findFirstParent(psiElement) { it.elementType == JFlexTypes.FLEX_LEXICAL_RULES_SECTION }?.let {
                        alignment = it.getUserData(LexicalRuleJavaBlockAlignKey)
                    }
                }
            }

        }
        return JFlexBlock(childNode, wrap, alignment, childIndent, spacingBuilder)
    }

    companion object {
        val AlignKey = Key<Alignment>("JFlexAlignKey")
        val LexicalRuleExprAlignKey = Key<Alignment>("JFlexLexicalRuleExprAlignKey")
        val LexicalRuleJavaBlockAlignKey = Key<Alignment>("JFlexLexicalRuleJavaBlockAlignKey")
    }

}