package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.Node

abstract class NodeBase<RuntimeContext, CategoryType> {
    abstract val builderContext: BuilderContext<RuntimeContext, CategoryType>

    enum class Type {
        Node,
        Choice,
        Narration,
        Action,
        Line,
        Say,
    }

    fun node(block: Node<RuntimeContext, CategoryType>.() -> Unit) =
        builderContext.nodeBuilder(builderContext, this, Type.Node).also {
            block(it)
            builderContext.registerNode(it)
        }

    fun choice(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ) = builderContext.nodeBuilder(builderContext, this, Type.Choice).also {
        it.chainTo = "@special.non-chainable"
        it.actor = builderContext.playerActor
        it.text = text
        block?.invoke(it)
        builderContext.registerNode(it)
    }

    fun choice(
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ) = builderContext.nodeBuilder(builderContext, this, Type.Choice).also {
        it.chainTo = "@special.non-chainable"
        it.actor = builderContext.playerActor
        it.textBuilder = textBuilder
        block?.invoke(it)
        builderContext.registerNode(it)
    }

    fun narration(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ): Node<RuntimeContext, CategoryType> {
        val node = builderContext.nodeBuilder(builderContext, this, Type.Narration)
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
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)?,
    ): Node<RuntimeContext, CategoryType> =
        builderContext.nodeBuilder(builderContext, this, Type.Narration).also {
            it.actor = builderContext.narratorActor
            it.textBuilder = textBuilder
            block?.invoke(it)
            builderContext.registerNode(it)
        }

    fun narration(textBuilder: RuntimeContext.() -> String): Node<RuntimeContext, CategoryType> = narration(textBuilder, null)

    fun line(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ): Node<RuntimeContext, CategoryType> {
        val node = builderContext.nodeBuilder(builderContext, this, Type.Line)
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
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)?,
    ): Node<RuntimeContext, CategoryType> =
        builderContext.nodeBuilder(builderContext, this, Type.Line).also {
            it.actor = builderContext.npcActor
            it.textBuilder = textBuilder
            block?.invoke(it)
            builderContext.registerNode(it)
        }

    fun line(textBuilder: RuntimeContext.() -> String): Node<RuntimeContext, CategoryType> = line(textBuilder, null)
}
