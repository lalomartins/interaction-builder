package info.lalomartins.games.interactionBuilder

import info.lalomartins.games.interactionBuilder.contexts.BuilderContext

class InteractionBuilder<
    ContextType : BuilderContext,
    // CategoryEnum, ActionBuilder
>(
    context: ContextType,
    script: ContextType.() -> Unit,
) {
    init {
        with(context) { script() }
    }
}

fun interactionBuilder(script: BuilderContext.Default.() -> Unit): InteractionBuilder<BuilderContext.Default> =
    InteractionBuilder(BuilderContext.Default(), script)
