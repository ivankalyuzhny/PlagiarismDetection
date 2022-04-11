import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class RabinKarp {
    private var matches = mutableMapOf<Int, PriorityQueue<Pair<Int, Int>>>()

    private val names = KotlinParser.tokenNames

    fun computeTiledTokensLength(pat: List<Tok>, txt: List<Tok>, minMatchLength: Int): Int {
        var s = 20
        var stop = false
        var lMax: Int
        while (!stop) {
            lMax = scanPattern(pat, txt, s)
            if (lMax > 2 * s) {
                s = lMax
            } else {
                markArrays(pat, txt, s)
                if (s > 2 * minMatchLength) {
                    s /= 2
                } else if (s > minMatchLength) {
                    s = minMatchLength
                } else {
                    stop = true
                }
            }
        }

        var tiledTokensLength = 0
        var p = 0
        while (p < pat.count()) {
            if (pat[p].isMarked) {
                tiledTokensLength++
            }
            p++
        }

        return tiledTokensLength
    }

    fun markArrays(pat: List<Tok>, txt: List<Tok>, s: Int) {
        matches.keys.sortedDescending().forEach { key ->
            // while not empty
            while (!matches.getValue(key).isEmpty()) {
                val match = matches.getValue(key).remove()
                val patStart = match.first
                val txtStart = match.second
                //println(key)
                var firstOccluded = -1
                var lastOccluded = key
                for (j in 0 until key) {
                    if (firstOccluded == -1 && (pat[patStart + j].isMarked or txt[txtStart + j].isMarked)) {
                        firstOccluded = j
                    }
                    if (lastOccluded == -1 && (pat[patStart + key - j - 1].isMarked or txt[txtStart + key - j - 1].isMarked)) {
                        lastOccluded = key - j - 1
                    }
                    if (firstOccluded != -1 && lastOccluded != -1) {
                        break
                    }
                }

                if (firstOccluded == -1 && lastOccluded == key) {
                    for (k in 0 until key) {
                        pat[patStart + k].isMarked = true
                        txt[txtStart + k].isMarked = true
                    }
                } else {
                    if (firstOccluded >= s) {
                        recordMatch(patStart, txtStart, firstOccluded)
                    }
                    if (key - 1 - lastOccluded >= s) {
                        recordMatch(patStart + lastOccluded + 1, txtStart + lastOccluded + 1, key - 1 - lastOccluded)
                    }
                }
            }
            /*matches.getValue(key).forEach { (patStart, txtStart) ->
                *//*val lOccluded =
                    (0 until key).map { pat[patStart + it].isMarked or txt[txtStart + it].isMarked }.foldIndexed()*//*
                // first false last true

            }*/
        }
    }

    fun scanPattern(pat: List<Tok>, txt: List<Tok>, s: Int): Int {
        val hashTable = HashMap<Int, Int>()
        var start: Int

        var maxMatchLength = s

        start = 0
        while (start < txt.count()) {
            if (!txt[start].isMarked) {
                var t = start
                var distance = 0
                while ((t + distance < txt.count()) && (!txt[t + distance].isMarked)) distance++
                if (distance < s) {
                    start += distance
                } else {
                    while (t < start + distance - s + 1) {
                        hashTable[t] = getHash(txt, t, s)
                        t++
                    }
                    start += distance
                }
            } else {
                start++
            }
        }

        start = 0
        while (start < pat.count()) {
            //println(hashTable.count())
            if (!pat[start].isMarked) {
                var p = start
                var distance = 0
                while ((p + distance < pat.count()) && (!pat[p + distance].isMarked)) distance++
                if (distance < s) {
                    start += distance
                } else {
                    while (p < start + distance - s + 1) {
                        val hash = getHash(pat, p, s)

                        hashTable.filter { it -> it.value == hash }.forEach {
                            //printTokens(txt, it.key, s)
                            var isEqual = true
                            var j = 0
                            while (j < s - 1 && isEqual) {
                                isEqual = pat[p + j].str == txt[it.key + j].str
                                j++
                            }
                            if (isEqual) {

                                var k = s
                                while ((p + k < pat.count() && it.key + k < txt.count()) && !pat[p + k].isMarked && !txt[it.key + k].isMarked && (pat[p + k].str == txt[it.key + k].str)) k++
                                if (k > 2 * s) {
                                    //println("k = $k")
                                    return (k)
                                } else {
                                    if (k > maxMatchLength) maxMatchLength = k
                                    recordMatch(p, it.key, k) // k instead s
                                }
                            }
                        }
                        p++
                    }
                    start += distance
                }
            } else {
                start++
            }
        }

        //println("maxMatchLength = $maxMatchLength")
        return maxMatchLength
    }

    fun getHash(seq: List<Tok>, start: Int, s: Int): Int {
        var hash = 0
        var t = start
        while (t < start + s) {
            hash = ((hash + names.indexOf(seq[t].str) % 101) * 256) % 101
            //hash+= names.indexOf(seq[t].str)
            t++
        }

        return hash
    }

    fun recordMatch(p: Int, t: Int, s: Int) {
        matches.getOrPut(s) { PriorityQueue<Pair<Int, Int>>(Comparator.comparingInt { it.first }) }.add(p to t)
        //matches[s]?.let { println(it.count()) }
    }

    fun printTokens(seq: List<Tok>, start: Int, s: Int) {
        var i = start
        while (i < start + s) {
            print(seq[i].str + " ")
            i++
        }
        println()
    }
}