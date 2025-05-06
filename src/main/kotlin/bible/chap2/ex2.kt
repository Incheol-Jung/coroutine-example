package bible.chap2

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 16.
 */
class ex2 {
}

fun main() = runBlocking<Unit> {
    println("[${Thread.currentThread().name}] 실행")
    launch {
        println("[${Thread.currentThread().name}] 실행")
    }
    launch {
        println("[${Thread.currentThread().name}] 실행")
    }
}