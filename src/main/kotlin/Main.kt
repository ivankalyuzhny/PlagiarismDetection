import org.antlr.v4.runtime.*
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

fun getTokens(path: String): String {
    val lexer = KotlinLexer(CharStreams.fromFileName(path))
    val tokens: TokenStream = CommonTokenStream(lexer)
    val parser = KotlinParser(tokens)
    parser.kotlinFile()
    var listTokens = emptyList<String>()
    for (i in 0 until tokens.size()) {
        val lexerRule: String = lexer.getVocabulary().getSymbolicName(tokens[i].type)
        if (lexerRule != "NL") //skipping NewLine tokens (for beauty)
            listTokens += lexerRule
    }
    return listTokens.joinToString(" ")
}

fun sortTokens(src: String): String {
    return src.substring(src.indexOf("FUN")).split("(?<=(FUN)))").sortedBy { it.length }.joinToString("")
        .replace("LineComment", "")
}

fun prepareData(string1: String): String {
    return string1.replace(Regex("//(\\S) "), "").replace(Regex("\\s\\s+"), " ")
}

fun main(args: Array<String>) {
    val string1 = prepareData(sortTokens(getTokens("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\AntlrTest\\src\\main\\kotlin\\test1.kt")))
    val string2 = prepareData(sortTokens(getTokens("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\AntlrTest\\src\\main\\kotlin\\test2.kt")))

    val x: Approach = TokenBased()

    println("""Score(string1 | string2): ${x.computeScore(string1, string2)}""")
}