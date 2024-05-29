package final

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 30.
 */
inline fun <T> retry(operation: () -> T): T {
    while (true) {
        try {
            return operation()
        } catch (e: Throwable) {
            // 처리안함
        }
    }
}

// 사용 예
suspend fun requestData(): String {
    if (Random.nextInt(0, 10) == 0) {
        return "ABC"
    } else {
        println("fail")
        delay(1000)
        error("Error")
    }
}

suspend fun main(): Unit = coroutineScope {
    println(retry { requestData() })
}