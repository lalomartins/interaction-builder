package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext
import info.lalomartins.games.interactionBuilder.contexts.SimpleRuntimeContext

class InteractionBuilder<
    ContextType,
    // CategoryEnum, ActionBuilder
>(
    rootContext: BuilderContext<ContextType> = BuilderContext.Simple(),
    script: BuilderContext<ContextType>.() -> Unit,
) {
    init {
        with(rootContext) { script() }
    }
}

fun interactionBuilder(script: BuilderContext<SimpleRuntimeContext>.() -> Unit) = InteractionBuilder(script = script)
