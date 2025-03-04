package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

abstract class BuilderContext<RuntimeContext, CategoryType> : NodeBase<RuntimeContext, CategoryType>() {
    override val builderContext: BuilderContext<RuntimeContext, CategoryType>
        get() = this
    var name = ""
    var introduction = ""
    var playerActor = "player"
    var npcActor = "npc"
    var narratorActor = ""
    var setup: (RuntimeContext.() -> Unit)? = null
    internal lateinit var nodeBuilder: (
        BuilderContext<RuntimeContext, CategoryType>,
        NodeBase<RuntimeContext, CategoryType>,
        Type,
    ) -> Node<RuntimeContext, CategoryType>

    fun introduction(text: String) {
        if (introduction.isNotEmpty()) {
            introduction += "\n\n"
        }
        introduction += text
    }

    fun setup(block: RuntimeContext.() -> Unit) {
        setup = block
    }

    override fun addChild(node: Node<RuntimeContext, CategoryType>) {}

    abstract fun registerNode(node: Node<RuntimeContext, CategoryType>)

    abstract fun lastNode(): Node<RuntimeContext, CategoryType>?

    abstract fun cleanup()

    abstract fun lastSibling(of: Node<RuntimeContext, CategoryType>): Node<RuntimeContext, CategoryType>?

    open class Simple<R, C> : BuilderContext<R, C>() {
        val nodes = mutableListOf<Node<R, C>>()
        val rootNodes = mutableListOf<Node<R, C>>()
        val nodeIndex = mutableMapOf<String, Node<R, C>>()

        override fun addChild(node: Node<R, C>) {
            rootNodes.add(node)
        }

        override fun nextSibling(node: Node<R, C>): Node<R, C>? {
            val i = rootNodes.indexOf(node)
            if (i != 1 && i < rootNodes.size - 1) {
                return rootNodes[i + 1]
            }
            return null
        }

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

        override fun cleanup() {
            for (node in nodes) {
                if (node.chain == null && node.chainTo != null) {
                    node.chain = nodeIndex[node.chainTo]
                }
                if (node.chain != null && node.children.isNotEmpty()) {
                    if (node.children.first().chainTo == MARKER_NON_CHAINABLE) {
                        for (child in node.children) {
                            if (child.chain == null && (child.chainTo == null || child.chainTo == MARKER_NON_CHAINABLE)) {
                                child.chain = node.chain
                                child.chainTo = node.chainTo
                            }
                        }
                    } else {
                        val tail = node.children.last()
                        if (tail.chain == null && tail.chainTo == null) {
                            tail.chain = node.chain
                            tail.chainTo = node.chainTo
                        }
                    }
                    node.chain = null
                    node.chainTo = null
                }
            }
        }

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
