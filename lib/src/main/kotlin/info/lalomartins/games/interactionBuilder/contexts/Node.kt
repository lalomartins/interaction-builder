package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

open class Node<RuntimeContext, CategoryType>(
    override val builderContext: BuilderContext<RuntimeContext, CategoryType>,
    val parent: NodeBase<RuntimeContext, CategoryType>? = null,
) : NodeBase<RuntimeContext, CategoryType>() {
    var anchor: String? = null
    var chain: Node<RuntimeContext, CategoryType>? = null
    var chainTo: String? = null
    var actor: String = builderContext.narratorActor
    var category: CategoryType? = null
    var text = ""
    var textBuilder: (RuntimeContext.() -> String)? = null
    var condition: (RuntimeContext.() -> Boolean)? = null
    val effects = mutableListOf<RuntimeContext.() -> Unit>()

    fun jump(to: String) {
        chainTo = to
    }

    fun needs(block: RuntimeContext.() -> Boolean) {
        condition = block
    }

    fun effect(block: RuntimeContext.() -> Unit) {
        effects.add(block)
    }
}
