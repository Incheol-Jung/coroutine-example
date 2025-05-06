package final

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 03.
 */
import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 03.
 */
class Page114 {
//    fun ex1() = runBlocking {
//        try {
//            launch {
//                delay(1000)
//                throw Error("Some Error")
//            }
//        } catch (e: Exception) {
//            println("Will not be printed")
//        }
//    }

    fun ex1() = runBlocking {
        val job = SupervisorJob()

        launch(job) {
//        launch {
//            launch(job) {
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
            launch {
                println("3")
                delay(3000)
                println("333")
            }
//            .cancel()
        }
        delay(3000)
        println("done")
    }

//    fun ex1() = runBlocking {
//        supervisorScope {
//            launch {
//                println("1")
//                delay(1000)
//                throw RuntimeException("some error")
//            }
//            launch {
//                println("2")
//                delay(2000)
//                println("1111")
//            }
//        }
////        delay(1000)
//    }
}

fun main() {
    with(Page114()) {
        ex1()
    }
}