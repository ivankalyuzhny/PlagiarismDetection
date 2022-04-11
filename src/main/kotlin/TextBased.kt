class TextBased : Approach {
    fun getTokenList(str: String): List<Tok> {
        var tokenList: MutableList<Tok> = arrayListOf()
        val strArr = str.split(" ")

        for (i in 1..strArr.count()) {
            tokenList.add(Tok(strArr[i - 1]))
        }
        return tokenList
    }

    override fun computeScore(string1: String, string2: String): Double {
        var tokenSequence1 = getTokenList(string1)
        var tokenSequence2 = getTokenList(string2)

        if (tokenSequence1.count() > tokenSequence2.count()) {
            tokenSequence1 = tokenSequence2.also { tokenSequence2 = tokenSequence1 }
        }

        var dist: Int
        var min = tokenSequence2.count()

        for (i in 1..tokenSequence2.count()) {
            dist = LevenshteinDistance().computeLevenshteinDistance(
                tokenSequence1,
                tokenSequence2.takeLast(tokenSequence2.count() - i + 1).plus(tokenSequence2.take(i - 1))
            )
            if (dist < min) {
                min = dist
            }
        }

        return 1 - min.toDouble() / tokenSequence2.count().toDouble()
    }
}