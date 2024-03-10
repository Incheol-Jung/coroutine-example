import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 10.
 */
suspend fun main(): Unit =
    coroutineScope {
        repeat(4000) {
//            CoroutineScope(Dispatchers.Default).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    println(Thread.currentThread().name)
                }
//            }
        }
        delay(3000)
    }