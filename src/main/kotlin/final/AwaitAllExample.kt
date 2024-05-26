package final

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
suspend fun fetchAsyncData(message: String): String {
    delay(1000)
    return message
}

fun main() = runBlocking {
    val defferedResults = listOf(
        async { fetchAsyncData("example 1") },
        async { fetchAsyncData("example 2") },
        async { fetchAsyncData("example 3") }
    )
    val times = measureTimedValue {
        val results = awaitAll(*defferedResults.toTypedArray())
        results.forEachIndexed { index, result ->
            println("${index} 결과 : $result")
        }
    }

    println(times)
}

