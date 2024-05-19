package final

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
class CallBackTest {
    suspend fun getNumbers(): List<Long> {
        delay(1000)
        return listOf(1, 2, 3, 4, 5)
    }

    suspend fun getNumbersWithCallback(): List<Long> {
        return suspendCancellableCoroutine { cont ->
            GlobalScope.launch {
                val numbers = getNumbers()
                cont.resume(numbers)
            }
        }
    }
}