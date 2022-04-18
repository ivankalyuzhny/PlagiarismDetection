class TokenBased : Approach {
    override fun computeScore(tokenSequence1: List<Tok>, tokenSequence2: List<Tok>): Double {
        var pattern = tokenSequence1
        var text = tokenSequence2
        if (pattern.count() > text.count())
            pattern = text.also { text = pattern }
        val minMatchLength = 5
        val score = RabinKarp().computeTiledTokensLength(pattern, text, minMatchLength)

        return score.toDouble() / pattern.count()
    }
}