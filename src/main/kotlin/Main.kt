import org.antlr.v4.runtime.*
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

fun getTokens(path: String): List<Tok> {
    val lexer = KotlinLexer(CharStreams.fromFileName(path))
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

fun sortTokens(src: String): String {
    return src.substring(src.indexOf("FUN")).split("(?<=(FUN)))").sortedBy { it.length }.joinToString("")
        .replace("LineComment", "")
}

fun printTokens(tokenSequence: List<Tok>, start: Int = 0, s: Int = tokenSequence.count()) {
    var i = start
    while (i < start + s) {
        print(tokenSequence[i].str + " ")
        i++
    }
    println()
}

fun main(args: Array<String>) {
    val tokenSequence1 = getTokens("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\PlagiarismDetection\\src\\main\\kotlin\\test1.kt")
    val tokenSequence2 = getTokens("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\PlagiarismDetection\\src\\main\\kotlin\\test2.kt")
    val x: Approach = TokenBased()

    printTokens(tokenSequence1)
    printTokens(tokenSequence2)
    println("""Score(string1 | string2): ${x.computeScore(tokenSequence1, tokenSequence2)}""")
}