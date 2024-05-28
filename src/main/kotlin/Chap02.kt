import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2023. 10. 02.
 */
fun main() = runBlocking {
    val time = measureTimedValue {
        val job1 = async { apiCall1() }
        val job2 = async { apiCall2() }
        printWithThread((job1.await() + job2.await()).toString())
    }
    printWithThread("소요 시간 : $time ms")
}

suspend fun apiCall1(): Int {
    delay(1000)
    return 1
}

suspend fun apiCall2(): Int {
    delay(1000)
    return 2
}