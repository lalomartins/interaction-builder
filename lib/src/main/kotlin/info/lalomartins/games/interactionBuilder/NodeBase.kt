package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.Node

abstract class NodeBase {
    abstract val builderContext: BuilderContext

    fun node(block: Node.() -> Unit) =
        Node(builderContext, this).also {
            block(it)
            builderContext.registerNode(it)
        }

    fun choice(
        text: String,
        block: (Node.() -> Unit)? = null,
    ) = Node(builderContext, this).also {
        it.chainTo = "@special.non-chainable"
        it.actor = builderContext.playerActor
        it.text = text
        block?.invoke(it)
        builderContext.registerNode(it)
    }

    fun choice(
        textBuilder: BuilderContext.() -> String,
        block: (Node.() -> Unit)? = null,
    ) = Node(builderContext, this).also {
        it.chainTo = "@special.non-chainable"
        it.actor = builderContext.playerActor
        it.textBuilder = textBuilder
        block?.invoke(it)
        builderContext.registerNode(it)
    }

    fun narration(
        text: String,
        block: (Node.() -> Unit)? = null,
    ): Node {
        val node = Node(builderContext, this)
        builderContext.lastSibling(node)?.let {
            if (it.actor == builderContext.narratorActor && it.textBuilder == null) {
                it.text += "\n\n" + text
                block?.invoke(it)
                return it
            }
        }
        node.actor = builderContext.narratorActor
        node.text = text
        block?.invoke(node)
        builderContext.registerNode(node)
        return node
    }

    fun narration(
        textBuilder: BuilderContext.() -> String,
        block: (Node.() -> Unit)?,
    ): Node =
        Node(builderContext, this).also {
            it.actor = builderContext.narratorActor
            it.textBuilder = textBuilder
            block?.invoke(it)
            builderContext.registerNode(it)
        }

    fun narration(textBuilder: BuilderContext.() -> String): Node = narration(textBuilder, null)

    fun line(
        text: String,
        block: (Node.() -> Unit)? = null,
    ): Node {
        val node = Node(builderContext, this)
        builderContext.lastSibling(node)?.let {
            if (it.actor == builderContext.npcActor && it.textBuilder == null) {
                it.text += "\n\n" + text
                block?.invoke(it)
                return it
            }
        }
        node.actor = builderContext.npcActor
        node.text = text
        block?.invoke(node)
        builderContext.registerNode(node)
        return node
    }

    fun line(
        textBuilder: BuilderContext.() -> String,
        block: (Node.() -> Unit)?,
    ): Node =
        Node(builderContext, this).also {
            it.actor = builderContext.npcActor
            it.textBuilder = textBuilder
            block?.invoke(it)
            builderContext.registerNode(it)
        }

    fun line(textBuilder: BuilderContext.() -> String): Node = line(textBuilder, null)

    fun effect(block: BuilderContext.() -> Unit) {
        block(builderContext)
    }
}
