package info.lalomartins.games.interactionBuilder.demo

import info.lalomartins.games.interactionBuilder.InteractionBuilder
import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.Node

class Runner<RuntimeContext>(
    private val interactionBuilder: InteractionBuilder<RuntimeContext, String>,
    private val runtimeContext: RuntimeContext,
) {
    val rootContext = interactionBuilder.rootContext as BuilderContext.Simple

    fun run() {
        rootContext.setup?.invoke(runtimeContext)
        if (rootContext.introduction.isNotEmpty()) {
            println(rootContext.introduction)
        }

        var node: Node<RuntimeContext, String>? = rootContext.rootNodes.firstOrNull()
        while (node != null) {
            node = runNode(node)
        }
    }

    private fun runNode(node: Node<RuntimeContext, String>): Node<RuntimeContext, String>? {
        if (node.condition?.invoke(runtimeContext) == false) {
            return getNextNode(node)
        }

        for (effect in node.effects) {
            effect.invoke(runtimeContext)
        }

        if (node.actor != rootContext.narratorActor) {
            println("${node.actor}:")
        }
        node.textBuilder?.invoke(runtimeContext)?.let {
            node.text = it
        }
        if (node.text.isNotEmpty()) {
            println(node.text)
        }

        val choices = node.children.filter { it.playerChoice }
        if (choices.isNotEmpty()) {
            val enabledMap = mutableMapOf<Int, Boolean>()
            for ((index, choice) in choices.withIndex()) {
                (choice.condition?.invoke(runtimeContext) != false).let { enabled ->
                    choice.textBuilder?.invoke(runtimeContext)?.let {
                        choice.text = it
                    }
                    enabledMap[index] = enabled
                    if (choice.text.isNotEmpty()) {
                        if (enabled) {
                            println("${index + 1}: (${choice.actor}) ${choice.text}")
                        } else {
                            println("—: (${choice.actor}) ${choice.text}")
                        }
                    }
                }
            }
            while (true) {
                val choice = readln().toInt() - 1
                if (choice < 0) {
                    return null
                }
                if (enabledMap[choice] == true) {
                    return choices[choice]
                } else {
                    println("Invalid choice")
                }
            }
        }

        node.children.firstOrNull { !it.playerChoice }?.let {
            return it
        }

        return node.chain ?: getNextNode(node)
    }

    private fun getNextNode(node: Node<RuntimeContext, String>): Node<RuntimeContext, String>? {
        node.parent?.nextSibling(node)?.let {
            return it
        }
        (node.parent as? Node<RuntimeContext, String>)?.let { parent ->
            parent.chain?.let {
                return it
            }
            return getNextNode(parent)
        }
        return null
    }
}
