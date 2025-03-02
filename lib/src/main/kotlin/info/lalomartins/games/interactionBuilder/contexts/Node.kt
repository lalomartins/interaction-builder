package info.lalomartins.games.interactionBuilder.contexts

import info.lalomartins.games.interactionBuilder.NodeBase

open class Node(
    val builderContext: BuilderContext,
    val parent: NodeBase? = null,
) {
    var anchor: String? = null
    var chain: Node? = null
    var chainTo: String? = null
    var actor: String = builderContext.narratorActor
    var text = ""
    var textBuilder: (BuilderContext.() -> String)? = null
    var condition: (BuilderContext.() -> Boolean)? = null

    fun jump(to: String) {
        chainTo = to
    }

    fun needs(block: BuilderContext.() -> Boolean) {
        condition = block
    }
}
