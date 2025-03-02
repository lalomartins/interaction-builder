package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.SimpleRuntimeContext

class InteractionBuilder<
    ContextType,
    CategoryType,
>(
    rootContext: BuilderContext<ContextType, CategoryType> = BuilderContext.Simple(),
    script: BuilderContext<ContextType, CategoryType>.() -> Unit,
) {
    init {
        with(rootContext) { script() }
    }
}

fun interactionBuilder(script: BuilderContext<SimpleRuntimeContext, String>.() -> Unit) = InteractionBuilder(script = script)
