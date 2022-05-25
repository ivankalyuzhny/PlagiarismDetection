import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream

class TextBased : Approach {
    fun getTokens(string: String): List<Tok> {
        val lexer = KotlinLexer(CharStreams.fromString(string))
        val tokens: TokenStream = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        parser.kotlinFile()
        var listTokens = emptyList<Tok>()
        for (i in 0 until tokens.size()) {
            val token = Tok(lexer.vocabulary.getSymbolicName(tokens[i].type), tokens[i].tokenIndex)
            if (token.str != "NL")
                listTokens += token
        }
        return listTokens
    }

    fun printTokens(tokenSequence: List<Tok>, start: Int = 0, s: Int = tokenSequence.count()) {
        var i = start
        while (i < start + s) {
            print(tokenSequence[i].str + " ")
            i++
        }
        println()
    }

    override fun computeScore(string1: String, string2: String): Double {
        var pattern = getTokens(string1)
        var text = getTokens(string2)

        if (pattern.count() > text.count()) {
            pattern = text.also { text = pattern }
        }
        //swap
        var dist: Int
        var min = text.count()

        for (i in 1..text.count()) {
            dist = LevenshteinDistance().computeLevenshteinDistance(
                pattern,
                text.takeLast(text.count() - i + 1).plus(text.take(i - 1))
            )
            if (dist < min) {
                min = dist
            }
        }

        return 1 - (min - (text.count() - pattern.count())).toDouble() / text.count().toDouble()
    }
}