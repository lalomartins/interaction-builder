package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

abstract class BuilderContext<RuntimeContext, CategoryType> : NodeBase<RuntimeContext, CategoryType>() {
    override val builderContext: BuilderContext<RuntimeContext, CategoryType>
        get() = this
    var name = ""
    var introduction = ""
    var playerActor = "player"
    var npcActor = "npc"
    var narratorActor = "narrator"
    var setup: (RuntimeContext.() -> Unit)? = null

    fun introduction(text: String) {
        if (introduction.isNotEmpty()) {
            introduction += "\n\n"
        }
        introduction += text
    }

    fun setup(block: RuntimeContext.() -> Unit) {
        setup = block
    }

    abstract fun registerNode(node: Node<RuntimeContext, CategoryType>)

    abstract fun lastNode(): Node<RuntimeContext, CategoryType>?

    abstract fun lastSibling(of: Node<RuntimeContext, CategoryType>): Node<RuntimeContext, CategoryType>?

    open class Simple<R, C> : BuilderContext<R, C>() {
        val nodes = mutableListOf<Node<R, C>>()
        val nodeIndex = mutableMapOf<String, Node<R, C>>()

        override fun registerNode(node: Node<R, C>) {
            lastSibling(node)?.let { other ->
                if (other.chain == null && other.chainTo == null) {
                    other.chain = node
                }
            }
            nodes.add(node)
            node.anchor?.let { nodeIndex[it] = node }
        }

        override fun lastNode() = nodes.lastOrNull()

        override fun lastSibling(of: Node<R, C>): Node<R, C>? {
            for (node in nodes.asReversed()) {
                if (node.parent == of.parent) {
                    return node
                }
            }
            return null
        }
    }
}
