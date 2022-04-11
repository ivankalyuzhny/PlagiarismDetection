class TokenBased : Approach {
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
        if (tokenSequence1.count() > tokenSequence2.count())
            tokenSequence1 = tokenSequence2.also { tokenSequence2 = tokenSequence1 }
        val minMatchLength = 5
        val score = RabinKarp().computeTiledTokensLength(tokenSequence1, tokenSequence2, minMatchLength)

        /*return if (score > tokenSequence1.count() - minMatchLength) {
            1.0
        } else {
            score.toDouble() / tokenSequence1.count()
        }*/

        /*return if (score > tokenSequence1.count() - minMatchLength) {
            (score + RabinKarp().computeTiledTokensLength(tokenSequence1, tokenSequence2, minMatchLength / 2)).toDouble() / 2 / tokenSequence1.count()
        } else {
            score.toDouble() / tokenSequence1.count()
        }*/

        return score.toDouble() / tokenSequence1.count()
    }
}