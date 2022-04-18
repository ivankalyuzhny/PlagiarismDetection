class TextBased : Approach {
    override fun computeScore(tokenSequence1: List<Tok>, tokenSequence2: List<Tok>): Double {
        var pattern = tokenSequence1
        var text = tokenSequence2

        if (pattern.count() > text.count()) {
            pattern = text.also { text = pattern }
        }

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

        return 1 - min.toDouble() / text.count().toDouble()
    }
}