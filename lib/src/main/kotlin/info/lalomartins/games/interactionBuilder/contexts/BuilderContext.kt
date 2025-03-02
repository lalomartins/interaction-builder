package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

abstract class BuilderContext : NodeBase() {
    override val builderContext: BuilderContext
        get() = this
    var name = ""
    var introduction = ""
    var playerActor = "player"
    var npcActor = "npc"
    var narratorActor = "narrator"

    fun introduction(text: String) {
        if (introduction.isNotEmpty()) {
            introduction += "\n\n"
        }
        introduction += text
    }

    abstract fun registerNode(node: Node)

    abstract fun lastNode(): Node?

    abstract fun lastSibling(of: Node): Node?

    open class Simple : BuilderContext() {
        val nodes = mutableListOf<Node>()
        val nodeIndex = mutableMapOf<String, Node>()

        override fun registerNode(node: Node) {
            lastSibling(node)?.let { other ->
                if (other.chain == null && other.chainTo == null) {
                    other.chain = node
                }
            }
            nodes.add(node)
            node.anchor?.let { nodeIndex[it] = node }
        }

        override fun lastNode() = nodes.lastOrNull()

        override fun lastSibling(of: Node): Node? {
            for (node in nodes.asReversed()) {
                if (node.parent == of.parent) {
                    return node
                }
            }
            return null
        }
    }

    open class Default : Simple() {
        val strings = mutableMapOf<String, String>()
        val vals = mutableMapOf<String, Float>()
        val flags = mutableMapOf<String, Boolean>()
    }
}
