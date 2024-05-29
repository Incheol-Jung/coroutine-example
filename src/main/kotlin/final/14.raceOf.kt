package final

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 30.
 */
suspend fun <T> raceOf(
    racer: suspend CoroutineScope.() -> T,
    vararg racers: suspend CoroutineScope.() -> T
): T = coroutineScope {
    select {
        (listOf(racer) + racers).forEach { racer ->
            async { racer() }.onAwait {
                coroutineContext.job.cancelChildren()
                it
            }
        }
    }
}

suspend fun fetchAsyncData2(message: String): String {
    delay(2000)
    return message
}

suspend fun fetchAsyncData3(message: String): String {
    delay(3000)
    return message
}

suspend fun main() {
    val result = raceOf(
        { fetchAsyncData2("example 1") },
        { fetchAsyncData3("example 2") }
    )

    println(result)
}