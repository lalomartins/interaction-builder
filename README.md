# Interaction Builder

Interaction Builder is a DSL for writing conversations in games. You can use it to write branching dialogue, and use that in a game engine.

It is inspired by [Yarn Spinner](https://www.yarnspinner.dev/), which is highly recommended if you're developing in anything but Kotlin (or possibly JVM). However, when I started writing a Yarn Spinner runtime for Kotlin, I realized one thing Kotlin is known for is being “the queen of DSLs”, so it would probably be easier and more intuitive to write a DSL instead.

No code is taken from or based on Yarn Spinner in any way, but some of the examples are adapted from theirs.

Okay, here's a simple example.

```kotlin
val simpleBuilder =
    interactionBuilder {
        name = "simple"

        introduction(
            """
            Interaction Builder is a DSL for writing conversations in games.
            You can use it to write branching dialogue, and use that in a game engine.
            
            This demo goes through the basic features. We recommend “playing” it while looking
            at the source code (`Simple.kt`).
            """.trimIndent(),
        )

        node {
            // The first node defined is the root node by default.
            choice("Wow, some options!") {
                narration("You got it, pal!")
                // a terminal like this is interpreted as "goto next sibling/uncle node"
            }

            choice("Can I put text inside options?") {
                narration(
                    """
                    You sure can!
                    For example, here's some lines inside an option.
                    You can even put options inside OTHER options!
                    """.trimIndent(),
                ) {
                    choice("Like this!") {
                        narration("Wow!")
                    }
                    choice("Or this!") {
                        narration("Incredible!")
                    }
                }
            }
        }
        
        narration("This would run after either branch above.") {
            effect {
                doSomethingAtRuntime()
            }
        }
    }
```

You can find more examples and details in the [demo](demo/src/main/kotlin/info/lalomartins/games/interactionBuilder/demo) project, along with a simple command line runner.

It is designed to be used in one of two ways:

## Simple games/projects

Get a root context by running the builder (with the defaults or with a custom runtime context class), then iterate that directly in your game. An example of that is included in the [demo](demo/src/main/kotlin/info/lalomartins/games/interactionBuilder/demo/Runner.kt) project.

## More complex games with custom needs

Get a root context by running the builder (with the defaults or with a custom node builder), then iterate all the nodes within to construct your own data structure as required by your game and/or engine.

```kotlin
class MyInteraction {
    fun fromBuilder(builder: InteractionBuilder) {
        val context = builder.context as BuilderContext.Simple
        for (node in builder.nodes) {
            convertNode(node)
        }
    }
}
```

## Installation

This library may or may not go on Maven Central once it's stable. For now, you can get it from [JitPack](https://jitpack.io/#lalomartins/interaction-builder).

```kotlin
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```

```kotlin
    dependencies {
        implementation("com.github.lalomartins:interaction-builder:lib:0.1.0")
    }
```

Note the actual imports are `info.lalomartins.interactionBuilder.*`, not `com.github.lalomartins.interaction-builder.*`.
