class LevenshteinDistance {
    fun computeLevenshteinDistance(
        tokenSequence1: List<Tok>,
        tokenSequence2: List<Tok>,
        insert_cost: Int = 1,
        delete_cost: Int = 1,
        replace_cost: Int = 1
    ): Int {
        if (tokenSequence1.count() > tokenSequence2.count()) {
            return computeLevenshteinDistance(tokenSequence2, tokenSequence1, delete_cost, insert_cost, replace_cost)
        }

        val minSize = tokenSequence1.count()
        val maxSize = tokenSequence2.count()

        val levDist = IntArray(minSize + 1)
        levDist[0] = 0

        (1..minSize).forEach { i -> levDist[i] = levDist[i - 1] + delete_cost }

        for (j in 1..maxSize) {
            var previousDiagonal = levDist[0]
            var previousDiagonalSave: Int

            levDist[0] += insert_cost

            for (i in 1..minSize) {
                previousDiagonalSave = levDist[i]
                if (tokenSequence1[i - 1].str == tokenSequence2[j - 1].str) {
                    levDist[i] = previousDiagonal
                } else {
                    levDist[i] = minOf(
                        minOf(levDist[i - 1] + delete_cost, levDist[i] + insert_cost),
                        previousDiagonal + replace_cost
                    )
                }
                previousDiagonal = previousDiagonalSave
            }
        }

        return levDist[minSize]
    }
}