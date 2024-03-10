import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 03.
 */
class Page115 {
    suspend fun ex1() = coroutineScope {
        val job = SupervisorJob()
        launch(job) {
            launch {
                println("1")
                delay(1000)
                throw Error("test")
            }
            launch {
                println("2")
                delay(2000)
                println("222")
            }
        }
        delay(3000)
        println("done")
    }
}

suspend fun main() {
    with(Page115()){
        ex1()
    }
}