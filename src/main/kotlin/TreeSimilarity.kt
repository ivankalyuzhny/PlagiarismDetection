import java.util.*

class TreeSimilarity {
    fun peekMax(l: PriorityQueue<ASTNode>): Int {
        return l.peek().getHeight() // not sure
    }

    fun push(t: ASTNode, l: PriorityQueue<ASTNode>) {
        l.add(t)
    }

    fun pop(l: PriorityQueue<ASTNode>): List<ASTNode> {
        val heightMax = l.peek().getHeight()

        var res = emptyList<ASTNode>()

        while (l.isNotEmpty() && l.peek().getHeight() == heightMax) {
            res += l.poll()
        }

        return res
    }

    fun open(t: ASTNode, l: PriorityQueue<ASTNode>) {
        t.children.forEach {
            l.add(it)
        }
    }

    fun isomorphic(t1: ASTNode, t2: ASTNode): Boolean {
        if (t1.children.count() != t2.children.count()){
            return false
        }
        else{
            for (i in 0..t1.children.count() - 1) {
                if(!isomorphic(t1.children[i], t2.children[i])) {
                    return false
                }
            }
            return true
        }
    }

    fun dice(t1: ASTNode, t2: ASTNode, M: List<Pair<ASTNode, ASTNode>>): Double {
        return 2 * t1.children.count { it -> M.contains(Pair(it, t2)) }.toDouble() / (t1.children.count() + t2.children.count())
    }

    fun topDown(T1: ASTNode, T2: ASTNode, minHeight: Int): List<Pair<ASTNode, ASTNode>> {
        var L1 = PriorityQueue<ASTNode>(compareBy { it.getHeight() })
        var L2 = PriorityQueue<ASTNode>(compareBy { it.getHeight() })
        var A = emptyList<Pair<ASTNode, ASTNode>>()
        var M = emptyList<Pair<ASTNode, ASTNode>>()

        push(T1, L1)
        push(T2, L2)

        while (minOf(peekMax(L1), peekMax(L2)) > minHeight) {
            if (peekMax(L1) != peekMax(L2)) {
                if (peekMax(L1) > peekMax(L2)) {
                    pop(L1).forEach {
                        L1.add(it)
                    }
                } else {
                    pop(L2).forEach {
                        L2.add(it)
                    }
                }
            }
            else {
                val H1 = pop(L1)
                val H2 = pop(L2)

                for (i in 0..H1.count() - 1){
                    for (j in 0..H2.count() - 1){
                        if (isomorphic(H1[i], H2[j])) {
                            if(T2.children.count { isomorphic(H1[i], it) && it != H2[j]} > 0 || T1.children.count { isomorphic(it, H2[j]) && it != H1[i]} > 0){
                                A += Pair(H1[i], H2[j])
                            }
                            else{
                                for (m in 0..H1[i].children.count() - 1) {
                                    for (n in 0..H2[j].children.count() - 1) {
                                        if (isomorphic(H1[i].children[m], H2[j].children[n])) {
                                            M += Pair(H1[i].children[m], H2[j].children[n])
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (i in 0..H1.count() - 1){
                    if (A.count { it.first == H1[i] } == 0 && M.count { it.first == H1[i] } == 0){
                        open(H1[i], L1)
                    }
                }

                for (j in 0..H2.count() - 1){
                    if (A.count { it.second == H2[j] } == 0 && M.count { it.second == H2[j] } == 0){
                        open(H2[j], L2)
                    }
                }
            }
        }

        A.sortedBy { it.first.parent?.let { it1 -> it.second.parent?.let { it2 -> dice(it1, it2, M) } } }

        while (A.isNotEmpty()){
            val pair = A.last()
            A = A.dropLast(1)

            for (i in 0..pair.first.children.count() - 1) {
                for (j in 0..pair.second.children.count() - 1) {
                    if (isomorphic(pair.first.children[i], pair.second.children[j])) {
                        M += Pair(pair.first.children[i], pair.second.children[j])
                    }
                }
            }

            A = A.filter { it.first != pair.first && it.second != pair.second }
        }
        return M
    }

    fun bottomUp(T1: ASTNode, T2: ASTNode, M: List<Pair<ASTNode, ASTNode>>) {

    }
}