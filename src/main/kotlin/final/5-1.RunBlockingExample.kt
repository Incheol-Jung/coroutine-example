package final

import kotlinx.coroutines.*
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
class RunBlockingExample {
    private suspend fun getNumbers(): List<Int> {
        delay(1000)
        return listOf(1, 2, 3, 4, 5)
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

class DiscSaveRepository {
    suspend fun loadSave(name: String): String {
        val temp = withContext(Dispatchers.IO){
            toUpperCase(name)
        }
        return temp
    }

    fun toUpperCase(name: String): String {
        return name.uppercase()
    }
}
suspend fun main() {
    val example = DiscSaveRepository()
    val result = example.loadSave("test")
    println(result)
}