package bible.chap2

import kotlinx.coroutines.runBlocking

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 16.
 */
class DebugTest {
}

fun main() = runBlocking {
    println("[${Thread.currentThread().name}]")
}