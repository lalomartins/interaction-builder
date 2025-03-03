package info.lalomartins.games.interactionBuilder.demo

import info.lalomartins.games.interactionBuilder.contexts.SimpleRuntimeContext
import info.lalomartins.games.interactionBuilder.interactionBuilder

fun SimpleRuntimeContext.fadeUp(duration: Float) {
    println("fadeUp($duration)")
}

val simpleBuilder =
    interactionBuilder {
        name = "simple"

        introduction(
            """
               |Interaction Builder is a DSL for writing conversations in games!
               |You can use it to write branching dialogue, and use that in a game engine!
               |
               |This demo goes through the basic features. We recommend “playing” it while looking
               |at the source code (`Simple.kt`).
            """.trimMargin(),
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
                    |You sure can!
                    |For example, here's some lines inside an option.
                    |You can even put options inside OTHER options!
                    """.trimMargin(),
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

        node {
            narration(
                """
                |You can also write 'effects', which represent things that happen in the game!
                |You can use 'effects' to write things like 'player takes damage'.
                """.trimIndent(),
            )

            effect {
                fadeUp(0.5f)
            }

            choice("Nice!") {
                narration("Thanks!")
            }

            choice("But it didn't actually fade!") {
                narration(
                    """
                    |That's because this demo doesn't know about 'fading', or any other feature. 
                    |For the demo, we just defined that function with a `println()`.
                    |
                    |In a real game, you can define custom effects that do useful work!
                    """.trimIndent(),
                )
            }
        }

        node {
            narration(
                """
                |The default context class has MutableMaps called `strings`, `vals`, and `flags`
                |set up for you.
                |You can use them to store state between nodes.
                |Let's set a string variable called "name".
                """.trimIndent(),
            )

            effect {
                strings["name"] = "Bob"
            }

            narration {
                """
                |You can use these variables in text, but for that you must pass them as a block
                |rather than a fixed string.
                |For example, here's a string that uses the name variable:
                |My name is ${strings["name"]}
                """.trimIndent()
            }

            choice("What can I store in a variable?") {
                narration("You can store strings in `strings`, numbers (float) in `vals`, and booleans in `flags`!")
            }

            choice("Where do variables get stored?") {
                narration(
                    """
                    Note these variable maps only exist if you use the default context class.
                    If you define your own context class, you can use whatever you want to store variables in,
                    and then you'll probably store them directly in properties rather than maps.
                    If you use the default context, you can either persist it and share it between interactions,
                    or instantiate a new one for each interaction (and then set it up in the `setup` block).
                    """.trimIndent(),
                )
            }
        }

        node {
            narration(
                """
                |We can also have conditional choices and even nodes.
                |Let's set a val called "gold" to 5.
                """.trimIndent(),
            )

            effect {
                vals["gold"] = 5F
            }
        }

        node {
            anchor = "gold check"

            narration("Next, let's enable different choices depending on what's stored inside `vals[\"gold\"]`:")

            choice({ "I have ${vals["gold"]} gold!" }) {
                needs { vals["gold"]!! >= 5 }
                line("You can buy an item!")
                line("You can buy a weapon!")
            }

            choice({ "I have ${vals["gold"]} gold!" }) {
                needs { vals["gold"]!! < 5 }
                line("You can't buy an item!")
            }
        }

        node {
            narration("Finally, we can use the \"jump\" command to go to a different node! Let's do that now!")

            choice("Please give me gold") {
                line("Sure!")
                effect {
                    vals["gold"] = vals["gold"]!! + 1
                }
                jump("gold check")
            }

            choice("Here, have some gold") {
                line("Thanks!")
                effect {
                    vals["gold"] = vals["gold"]!! - 1
                }
                jump("gold check")
            }

            choice("I'm done") {
                narration("Thanks for playing!")
            }
        }
    }
