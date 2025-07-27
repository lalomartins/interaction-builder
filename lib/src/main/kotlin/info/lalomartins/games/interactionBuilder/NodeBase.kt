package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.Node

abstract class NodeBase<RuntimeContext, CategoryType> {
    abstract val builderContext: BuilderContext<RuntimeContext, CategoryType>

    enum class Type {
        Node,
        Action,
        Dialog,
        Narration,
    }

    abstract fun addChild(node: Node<RuntimeContext, CategoryType>)

    abstract fun nextSibling(node: Node<RuntimeContext, CategoryType>): Node<RuntimeContext, CategoryType>?

    fun node(block: Node<RuntimeContext, CategoryType>.() -> Unit) =
        builderContext.nodeBuilder(builderContext, this, Type.Node).also {
            block(it)
            addChild(it)
            builderContext.registerNode(it)
        }

    fun choice(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ) = builderContext.nodeBuilder(builderContext, this, Type.Action).apply {
        chainTo = MARKER_NON_CHAINABLE
        playerChoice = true
        actor = builderContext.playerActor
        this.text = text
        block?.invoke(this)
        this@NodeBase.addChild(this)
        builderContext.registerNode(this)
    }

    fun choice(
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ) = builderContext.nodeBuilder(builderContext, this, Type.Action).apply {
        chainTo = MARKER_NON_CHAINABLE
        playerChoice = true
        actor = builderContext.playerActor
        this.textBuilder = textBuilder
        block?.invoke(this)
        this@NodeBase.addChild(this)
        builderContext.registerNode(this)
    }

    fun narration(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)?,
    ): Node<RuntimeContext, CategoryType> {
        val node = builderContext.nodeBuilder(builderContext, this, Type.Narration)
        builderContext.lastSibling(node)?.let {
            if (it.actor == builderContext.narratorActor && it.textBuilder == null && it.children.isEmpty()) {
                if (it.text.isNotEmpty()) {
                    it.text += "\n\n"
                }
                it.text += text
                block?.invoke(it)
                return it
            }
        }
        node.actor = builderContext.narratorActor
        node.text = text
        block?.invoke(node)
        addChild(node)
        builderContext.registerNode(node)
        return node
    }

    open fun narration(text: String): Node<RuntimeContext, CategoryType> = narration(text, null)

    fun narration(
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)?,
    ): Node<RuntimeContext, CategoryType> =
        builderContext.nodeBuilder(builderContext, this, Type.Narration).apply {
            actor = builderContext.narratorActor
            this.textBuilder = textBuilder
            block?.invoke(this)
            this@NodeBase.addChild(this)
            builderContext.registerNode(this)
        }

    open fun narration(textBuilder: RuntimeContext.() -> String): Node<RuntimeContext, CategoryType> = narration(textBuilder, null)

    fun line(
        text: String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)? = null,
    ): Node<RuntimeContext, CategoryType> {
        val node = builderContext.nodeBuilder(builderContext, this, Type.Dialog)
        builderContext.lastSibling(node)?.let {
            if (it.actor == builderContext.npcActor && it.textBuilder == null && it.children.isEmpty()) {
                if (it.text.isNotEmpty()) {
                    it.text += "\n\n"
                }
                it.text += "\n\n" + text
                block?.invoke(it)
                return it
            }
        }
        node.actor = builderContext.npcActor
        node.text = text
        block?.invoke(node)
        addChild(node)
        builderContext.registerNode(node)
        return node
    }

    fun line(
        textBuilder: RuntimeContext.() -> String,
        block: (Node<RuntimeContext, CategoryType>.() -> Unit)?,
    ): Node<RuntimeContext, CategoryType> =
        builderContext.nodeBuilder(builderContext, this, Type.Dialog).apply {
            actor = builderContext.npcActor
            this.textBuilder = textBuilder
            block?.invoke(this)
            this@NodeBase.addChild(this)
            builderContext.registerNode(this)
        }

    fun line(textBuilder: RuntimeContext.() -> String): Node<RuntimeContext, CategoryType> = line(textBuilder, null)

    companion object {
        const val MARKER_NON_CHAINABLE = "@special.non-chainable"
    }
}
