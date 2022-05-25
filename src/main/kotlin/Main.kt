import org.antlr.v4.runtime.*
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

/*fun sortTokens(src: String): String {
    return src.substring(src.indexOf("FUN")).split("(?<=(FUN)))").sortedBy { it.length }.joinToString("")
        .replace("LineComment", "")
}*/

fun main(args: Array<String>) {
    val string1 = File("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\PlagiarismDetection\\src\\main\\kotlin\\test1.kt").readText(Charsets.UTF_8)
    val string2 = File("C:\\Users\\ivank\\OneDrive\\Рабочий стол\\НИР\\PlagiarismDetection\\src\\main\\kotlin\\test2.kt").readText(Charsets.UTF_8)
    val x: Approach = TreeBased()

    println("""Score(string1 | string2): ${x.computeScore(string1, string2)}""")
}