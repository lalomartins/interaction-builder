package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

abstract class BuilderContext<RuntimeContext> : NodeBase<RuntimeContext>() {
    override val builderContext: BuilderContext<RuntimeContext>
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

    abstract fun registerNode(node: Node<RuntimeContext>)

    abstract fun lastNode(): Node<RuntimeContext>?

    abstract fun lastSibling(of: Node<RuntimeContext>): Node<RuntimeContext>?

    open class Simple<R> : BuilderContext<R>() {
        val nodes = mutableListOf<Node<R>>()
        val nodeIndex = mutableMapOf<String, Node<R>>()

        override fun registerNode(node: Node<R>) {
            lastSibling(node)?.let { other ->
                if (other.chain == null && other.chainTo == null) {
                    other.chain = node
                }
            }
            nodes.add(node)
            node.anchor?.let { nodeIndex[it] = node }
        }

        override fun lastNode() = nodes.lastOrNull()

        override fun lastSibling(of: Node<R>): Node<R>? {
            for (node in nodes.asReversed()) {
                if (node.parent == of.parent) {
                    return node
                }
            }
            return null
        }
    }
}
