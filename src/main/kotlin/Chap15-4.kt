import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.random.Random
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 09.
 */
fun main() {
    val dispatcher = StandardTestDispatcher()

    CoroutineScope(dispatcher).launch {
        delay(1000)
        println("Coroutine done")
    }

    Thread.sleep(Random.nextLong(2000))

    val time = measureTimedValue {
        println("[${dispatcher.scheduler.currentTime}] Before")
//        dispatcher.scheduler.advanceUntilIdle()
        println("[${dispatcher.scheduler.currentTime}] After")
    }

    println("Took $time ms")
}