import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2023. 10. 02.
 */
fun main(): Unit = runBlocking {
    runBlocking {
        withContext(SupervisorJob()) {
            async {
                println("test111")
                throw RuntimeException("test")
            }
        }

        withContext(SupervisorJob()) {
            async {
                println("test222")
            }
        }

    }
}