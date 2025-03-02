package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

open class Node<RuntimeContext>(
    override val builderContext: BuilderContext<RuntimeContext>,
    val parent: NodeBase<RuntimeContext>? = null,
) : NodeBase<RuntimeContext>() {
    var anchor: String? = null
    var chain: Node<RuntimeContext>? = null
    var chainTo: String? = null
    var actor: String = builderContext.narratorActor
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
