import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class RabinKarp {
    private var matches = mutableMapOf<Int, PriorityQueue<Pair<Int, Int>>>()
    private var matchList = emptyList<Match>()
    private val names = KotlinParser.tokenNames

    fun computeTiledTokensLength(pattern: List<Tok>, text: List<Tok>, minMatchLength: Int): Int {
        var searchLength = 20
        var stop = false
        var lMax: Int
        while (!stop) {
            lMax = scanPattern(pattern, text, searchLength)
            if (lMax > 2 * searchLength) {
                searchLength = lMax
            } else {
                markArrays(pattern, text, searchLength)
                if (searchLength > 2 * minMatchLength) {
                    searchLength /= 2
                } else if (searchLength > minMatchLength) {
                    searchLength = minMatchLength
                } else if (searchLength > minMatchLength) {
                    searchLength = minMatchLength
                } else if (searchLength > 3 && getTiledTokensLength(pattern) > pattern.count() - minMatchLength) {
                    searchLength--
                } else {
                    stop = true
                }
            }
        }

        return getTiledTokensLength(pattern)
    }

    fun getMatchList(pattern: List<Tok>, text: List<Tok>, minMatchLength: Int): List<Match> {
        matchList = emptyList()
        var searchLength = 20
        var stop = false
        var lMax: Int
        while (!stop) {
            lMax = scanPattern(pattern, text, searchLength)
            if (lMax > 2 * searchLength) {
                searchLength = lMax
            } else {
                markArrays(pattern, text, searchLength, true)
                if (searchLength > 2 * minMatchLength) {
                    searchLength /= 2
                } else if (searchLength > minMatchLength) {
                    searchLength = minMatchLength
                } else if (searchLength > minMatchLength) {
                    searchLength = minMatchLength
                } else if (searchLength > 3 && getTiledTokensLength(pattern) > pattern.count() - minMatchLength) {
                    searchLength--
                } else {
                    stop = true
                }
            }
        }

        return matchList
    }

    private fun markArrays(pattern: List<Tok>, text: List<Tok>, searchLength: Int, getMatchList: Boolean = false) {
        matches.keys.sortedDescending().forEach { key ->
            // while not empty
            while (!matches.getValue(key).isEmpty()) {
                val match = matches.getValue(key).remove()
                val patternStartMatch = match.first
                val textStartMatch = match.second
                //println(key)
                var firstOccluded = -1
                var lastOccluded = key
                for (j in 0 until key) {
                    if (firstOccluded == -1 && (pattern[patternStartMatch + j].isMarked or text[textStartMatch + j].isMarked)) {
                        firstOccluded = j
                    }
                    if (lastOccluded == -1 && (pattern[patternStartMatch + key - j - 1].isMarked or text[textStartMatch + key - j - 1].isMarked)) {
                        lastOccluded = key - j - 1
                    }
                    if (firstOccluded != -1 && lastOccluded != -1) {
                        break
                    }
                }

                if (firstOccluded == -1 && lastOccluded == key) {
                    for (k in 0 until key) {
                        pattern[patternStartMatch + k].isMarked = true
                        text[textStartMatch + k].isMarked = true
                    }
                    if (getMatchList){
                        matchList = matchList + Match(
                            pattern[patternStartMatch].position,
                            text[textStartMatch].position,
                            pattern[patternStartMatch + key - 1].position,
                            text[textStartMatch + key - 1].position
                        )
                    }
                } else {
                    if (firstOccluded >= searchLength) {
                        recordMatch(patternStartMatch, textStartMatch, firstOccluded)
                    }
                    if (key - 1 - lastOccluded >= searchLength) {
                        recordMatch(
                            patternStartMatch + lastOccluded + 1,
                            textStartMatch + lastOccluded + 1,
                            key - 1 - lastOccluded
                        )
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

    private fun scanPattern(pattern: List<Tok>, text: List<Tok>, searchLength: Int): Int {
        val hashTable = HashMap<Int, Int>()
        var maxMatchLength = searchLength

        var start = 0
        while (start < text.count()) {
            if (!text[start].isMarked) {
                var t = start
                var distance = 0
                while ((t + distance < text.count()) && (!text[t + distance].isMarked)) distance++
                if (distance < searchLength) {
                    start += distance
                } else {
                    while (t < start + distance - searchLength + 1) {
                        hashTable[t] = getHash(text, t, searchLength)
                        t++
                    }
                    start += distance
                }
            } else {
                start++
            }
        }

        start = 0
        while (start < pattern.count()) {
            if (!pattern[start].isMarked) {
                var p = start
                var distance = 0
                while ((p + distance < pattern.count()) && (!pattern[p + distance].isMarked)) distance++
                if (distance < searchLength) {
                    start += distance
                } else {
                    while (p < start + distance - searchLength + 1) {
                        val hash = getHash(pattern, p, searchLength)

                        hashTable.filter { it.value == hash }.forEach {
                            var isEqual = true
                            var j = 0
                            while (j < searchLength - 1 && isEqual) {
                                isEqual = pattern[p + j].str == text[it.key + j].str
                                j++
                            }
                            if (isEqual) {
                                var k = searchLength
                                while ((p + k < pattern.count() && it.key + k < text.count()) && !pattern[p + k].isMarked && !text[it.key + k].isMarked && (pattern[p + k].str == text[it.key + k].str)) k++
                                if (k > 2 * searchLength) {
                                    return (k)
                                } else {
                                    if (k > maxMatchLength) maxMatchLength = k
                                    recordMatch(p, it.key, k)
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

        return maxMatchLength
    }

    private fun getHash(tokenSequence: List<Tok>, start: Int, s: Int): Int {
        var hash = 0
        var t = start
        while (t < start + s) {
            hash = ((hash + names.indexOf(tokenSequence[t].str) % 101) * 256) % 101
            t++
        }

        return hash
    }

    private fun recordMatch(p: Int, t: Int, s: Int) {
        matches.getOrPut(s) { PriorityQueue<Pair<Int, Int>>(Comparator.comparingInt { it.first }) }.add(p to t)
    }

    private fun getTiledTokensLength(tokenSequence: List<Tok>): Int {
        return tokenSequence.count { it.isMarked }
    }

    fun printTokens(tokenSequence: List<Tok>, start: Int = 0, s: Int = tokenSequence.count()) {
        var i = start
        while (i < start + s) {
            print(tokenSequence[i].str + " ")
            i++
        }
        println()
    }
}