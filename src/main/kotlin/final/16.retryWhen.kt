package final

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.pow


/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 30.
 */
inline fun <T> retryWhen(
    predicate: (Throwable, retires: Int) -> Boolean,
    operation: () -> T
): T {
    var retries = 0
    var fromDownStream: Throwable? = null
    while (true) {
        try {
            return operation()
        } catch (e: Throwable) {
            if (fromDownStream != null) {
                e.addSuppressed(fromDownStream)
            }
            fromDownStream = e
            if (e is CancellationException || !predicate(e, retries++)) {
                throw e
            }
        }
    }
}

suspend fun requestWithRetry(attempt: Int) = retryWhen(
    predicate = { e, retries ->
        val times = 2.0.pow(attempt.toDouble()).toInt()
        delay(maxOf(10_000L, 100L * times))
        println("retried")
        retries < 10 && e is IllegalStateException
    }
) {
    requestData()
}

suspend fun main(): Unit = coroutineScope {
    println(requestWithRetry(10))
}