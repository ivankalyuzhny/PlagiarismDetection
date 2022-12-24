import java.util.*

class TreeSimilarity {
    fun PriorityQueue<TreeNode>.peekMaxHeight(): Int {
        return this.peek()?.height ?: 0
    }

    fun PriorityQueue<TreeNode>.popAllWithMaxHeight(): List<TreeNode> {
        return if (isEmpty()) {
            emptyList()
        } else {
            val heightMax = peekMaxHeight()
            val res = mutableListOf<TreeNode>()
            while (peekMaxHeight() == heightMax) {
                res += poll()
            }
            res
        }

    }

    fun open(t: TreeNode, l: PriorityQueue<TreeNode>) {
        t.children.forEach {
            l.add(it)
        }
    }

    fun isomorphic(t1: TreeNode, t2: TreeNode): Boolean {
        TODO("избавиться от рекурсии")
        if (t1.children.count() != t2.children.count()) {
            return false
        } else {
            for (i in 0 until t1.children.count()) {
                if (!isomorphic(t1.children[i], t2.children[i])) {
                    return false
                }
            }
            return true
        }
    }

    fun dice(t1: TreeNode?, t2: TreeNode?, M: List<Pair<TreeNode, TreeNode>>): Double {
        if (t1 == null || t2 == null) return .0
        return 2 * t1.children.count { M.contains(it to t2) }
            .toDouble() / (t1.children.count() + t2.children.count())
    }

    fun topDown(T1: TreeNode, T2: TreeNode, minHeight: Int): List<Pair<TreeNode, TreeNode>> {
        val L1 = PriorityQueue<TreeNode>(compareBy { it.height })
        val L2 = PriorityQueue<TreeNode>(compareBy { it.height })
        val A = mutableListOf<Pair<TreeNode, TreeNode>>()
        val M = mutableListOf<Pair<TreeNode, TreeNode>>()

        L1.add(T1)
        L2.add(T2)

        while (minOf(L1.peekMaxHeight(), L2.peekMaxHeight()) > minHeight) {
            if (L1.peekMaxHeight() != L2.peekMaxHeight()) {
                val queue = if (L1.peekMaxHeight() > L2.peekMaxHeight()) {
                    L1
                } else {
                    L2
                }
                queue.popAllWithMaxHeight().asSequence().flatMap(TreeNode::children).forEach(queue::add)
            } else {
                val H1 = L1.popAllWithMaxHeight()
                val H2 = L2.popAllWithMaxHeight()

                // в H1 найти все изоморфные между собой деревья
                // в H2 -//-

                // для групп из H1
                for (i in 0 until H1.count()) {
                    // для недобавленных групп из H2
                    for (j in 0 until H2.count()) {
                        // если пара произвольных элементов из разных групп изоморфна
                        if (isomorphic(H1[i], H2[j])) {
                            // если размеры обеих групп равны 1
                            if (
                                H2.asSequence().filter { it != H2[j] }.any { isomorphic(H1[i], it) }
                                //TODO
                                || T1.children.count { isomorphic(it, H2[j]) && it != H1[i] } > 0
                            ) {
                                A += H1[i] to H2[j]
                            } else {
                                for (m in 0 until H1[i].children.count()) {
                                    for (n in 0 until H2[j].children.count()) {
                                        if (isomorphic(H1[i].children[m], H2[j].children[n])) {
                                            M += H1[i].children[m] to H2[j].children[n]
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // можно упростить: проверить, что всё, что не добавлено в A не лежит в M
                for (i in 0 until H1.count()) {
                    if (A.any { it.first == H1[i] } || M.any { it.first == H1[i] }) {
                        open(H1[i], L1)
                    }
                }

                for (j in 0 until H2.count()) {
                    if (A.count { it.second == H2[j] } == 0 && M.count { it.second == H2[j] } == 0) {
                        open(H2[j], L2)
                    }
                }
            }
        }

        A.sortedBy { dice(it.first.parent, it.second.parent, M) }.map {
            for (i in 0 until it.first.children.count()) {
                for (j in 0 until it.second.children.count()) {
                    if (isomorphic(it.first.children[i], it.second.children[j])) {
                        M += Pair(it.first.children[i], it.second.children[j])
                    }
                }
            }
        }
        return M
    }

    fun bottomUp(T1: TreeNode, T2: TreeNode, M: List<Pair<TreeNode, TreeNode>>) {

    }
}
