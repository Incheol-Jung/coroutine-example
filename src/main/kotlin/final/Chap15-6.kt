package final

import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 10.
 */
fun main(): Unit = runBlocking {
    launch {
        println("aaa")
        delay(2000)
        println("bbb")
    }
    launch {
        println("ddd")
        println("eee")
    }
}