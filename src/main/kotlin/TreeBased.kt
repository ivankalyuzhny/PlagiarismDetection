import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext

class TreeBased:Approach {
    private fun getAST(string: String): ASTNode {
        val lexer = KotlinLexer(CharStreams.fromString(string))
        val tokens = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        val ctx: ParserRuleContext = parser.kotlinFile()
        return buildAST(ctx)
    }

    private fun buildAST(ctx: RuleContext, parent: ASTNode? = null): ASTNode {
        var res = ASTNode(KotlinParser.ruleNames[ctx.ruleIndex], parent)
        for (i in 0 until ctx.childCount) {
            val element = ctx.getChild(i)
            if (element is RuleContext) {
                res.addChild(buildAST(element, res))
            }
        }

        return res
    }

    fun printAST(node : ASTNode, indentation : Int) {
        val ruleName: String = node.name
        for (i in 0 until indentation) {
            print("  ")
        }
        println(ruleName)

        for (i in 0 until node.children.count()) {
            val element = node.children[i]
            printAST(element, indentation + 1)
        }
    }

    override fun computeScore(string1: String, string2: String): Double {
        var pattern = getAST(string1)
        var text = getAST(string2)
        if (pattern.getHeight() > text.getHeight())
            pattern = text.also { text = pattern }

        printAST(pattern, 0)
        printAST(text, 0)
        val M = TreeSimilarity().topDown(pattern, text, 3)
        var len = 0
        M.forEach { len += it.first.getHeight() }
        println(len)
        return 0.0
    }
}