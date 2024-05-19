package final

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
class RunBlockingExample {
    private suspend fun getNumbers(): List<Int> {
        delay(1000)
        return listOf(1,2,3,4,5)
    }

    fun calculate() {
        val timedValue = measureTimedValue {
            runBlocking {
                val numbers = async { getNumbers() }
                val numbers2 = async { getNumbers() }
                numbers.await() + numbers2.await()
            }
        }
        println(timedValue)
    }
}

fun main() {
    val example = RunBlockingExample()
    example.calculate()
}