package final

import kotlinx.coroutines.*
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
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