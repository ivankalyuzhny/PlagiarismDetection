import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream

class TokenBased : Approach {
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
        if (pattern.count() > text.count())
            pattern = text.also { text = pattern }
        val minMatchLength = 5
        val score = RabinKarp().computeTiledTokensLength(pattern, text, minMatchLength)

        return score.toDouble() / pattern.count()
    }

    fun getMatchList(string1: String, string2: String): List<Match> {
        var pattern = getTokens(string1)
        var text = getTokens(string2)
        if (pattern.count() > text.count())
            pattern = text.also { text = pattern }
        val minMatchLength = 5

        return RabinKarp().getMatchList(pattern, text, minMatchLength)
    }
}