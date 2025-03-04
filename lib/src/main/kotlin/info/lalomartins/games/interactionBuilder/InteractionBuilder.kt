package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.Node
import info.lalomartins.games.interactionBuilder.contexts.SimpleRuntimeContext

class InteractionBuilder<
    ContextType,
    CategoryType,
>(
    val rootContext: BuilderContext<ContextType, CategoryType> = BuilderContext.Simple(),
    nodeBuilder: (
        BuilderContext<ContextType, CategoryType>,
        NodeBase<ContextType, CategoryType>,
        NodeBase.Type,
    ) -> Node<ContextType, CategoryType> =
        { builderContext, parent, type -> Node(builderContext, parent, type) },
    script: BuilderContext<ContextType, CategoryType>.() -> Unit,
) {
    init {
        with(rootContext) {
            this.nodeBuilder = nodeBuilder
            script()
        }
        rootContext.cleanup()
    }
}

fun interactionBuilder(script: BuilderContext<SimpleRuntimeContext, String>.() -> Unit) = InteractionBuilder(script = script)
