class ASTNode(name: String, parent: ASTNode? = null, isMarked: Boolean = false){
    var name = name
    var isMarked : Boolean = isMarked
    var parent = parent
    var children = mutableListOf<ASTNode>()
    fun addChild(child : ASTNode) {
        children.add(child)
    }
    fun getHeight(): Int {
        if (children.count() == 0) {
            return 1    
        } else{
            var max = 1
            children.forEach {
                val temp = it.getHeight()
                if (temp > max) {
                    max = temp
                }
            }
            return max + 1
        }
    }
}