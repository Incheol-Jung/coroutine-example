import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 09.
 */
fun main() {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done")
    }

    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done2")
    }

    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done3")
    }

    for (i in 1..5){
        print(".")
        testDispatcher.scheduler.advanceTimeBy(1)
        testDispatcher.scheduler.runCurrent()
    }
}