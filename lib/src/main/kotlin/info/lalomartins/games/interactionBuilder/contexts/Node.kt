package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

open class Node<RuntimeContext, CategoryType>(
    override val builderContext: BuilderContext<RuntimeContext, CategoryType>,
    val parent: NodeBase<RuntimeContext, CategoryType>?,
    val type: Type,
) : NodeBase<RuntimeContext, CategoryType>() {
    var anchor: String? = null
    var chain: Node<RuntimeContext, CategoryType>? = null
    var chainTo: String? = null
    val children = mutableListOf<Node<RuntimeContext, CategoryType>>()
    var actor: String = builderContext.narratorActor
    var playerChoice = false
    var category: CategoryType? = null
    var text = ""
    var textBuilder: (RuntimeContext.() -> String)? = null
    var condition: (RuntimeContext.() -> Boolean)? = null
    val effects = mutableListOf<RuntimeContext.() -> Unit>()

    override fun addChild(node: Node<RuntimeContext, CategoryType>) {
        children.add(node)
    }

    override fun nextSibling(node: Node<RuntimeContext, CategoryType>): Node<RuntimeContext, CategoryType>? {
        val i = children.indexOf(node)
        if (i != 1 && i < children.size - 1) {
            return children[i + 1]
        }
        return null
    }

    fun jump(to: String) {
        chainTo = to
    }

    fun needs(block: RuntimeContext.() -> Boolean) {
        condition = block
    }

    fun effect(block: RuntimeContext.() -> Unit) {
        effects.add(block)
    }

    override fun narration(text: String): Node<RuntimeContext, CategoryType> {
        if (actor == builderContext.narratorActor && textBuilder == null) {
            if (this.text.isNotEmpty()) {
                this.text += "\n\n"
            }
            this.text += text
            return this
        }
        return super.narration(text)
    }

    override fun narration(textBuilder: RuntimeContext.() -> String): Node<RuntimeContext, CategoryType> {
        if (actor == builderContext.narratorActor && this.textBuilder == null && this.text.isEmpty()) {
            this.textBuilder = textBuilder
            return this
        }
        return super.narration(textBuilder)
    }
}
