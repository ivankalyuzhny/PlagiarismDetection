import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import java.util.*

data class TreeNode(val name: String, val children: List<TreeNode> = emptyList()) {
    var parent: TreeNode? = null
    val height: Int = (children.maxOfOrNull { it.height } ?: 0) + 1
}

sealed class BuildContext(val context: RuleContext)
class SimpleContext(context: RuleContext) : BuildContext(context)
class ExpandedContext(context: RuleContext, val childCount: Int) : BuildContext(context)

class TreeBased : Approach {
    private fun getAST(string: String): TreeNode {
        val lexer = KotlinLexer(CharStreams.fromString(string))
        val tokens = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        val ctx: ParserRuleContext = parser.kotlinFile()
        return buildAST(ctx)
    }

    fun buildAST(ctx: RuleContext): TreeNode {

        val parseNodes = Stack<BuildContext>().apply {
            push(SimpleContext(ctx))
        }
        val nodes = Stack<TreeNode>()

        while (parseNodes.isNotEmpty()) {
            when (val parseNode = parseNodes.pop()) {
                is ExpandedContext -> {
                    val children = (0 until parseNode.childCount)
                        .asSequence()
                        .map { nodes.pop() }
                        .toList()
                    val node = TreeNode(KotlinParser.ruleNames[parseNode.context.ruleIndex], children)
                    children.forEach { it.parent = node }
                    nodes.push(node)
                }
                is SimpleContext -> {
                    val newContexts = (0 until parseNode.context.childCount).asSequence()
                        .map(parseNode.context::getChild)
                        .filterIsInstance<RuleContext>()
                        .map(::SimpleContext)
                        .toList()
                    parseNodes.push(ExpandedContext(parseNode.context, newContexts.count()))
                    newContexts.forEach(parseNodes::push)
                }
            }
        }
        return nodes.pop()
    }

    fun printAST(node: TreeNode, indentation: Int) {
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
        if (pattern.height > text.height) pattern = text.also { text = pattern }

        printAST(pattern, 0)
        printAST(text, 0)
        val M = TreeSimilarity().topDown(pattern, text, 3)
        var len = 0
        M.forEach { len += it.first.height }
        println(len)
        return 0.0
    }
}
