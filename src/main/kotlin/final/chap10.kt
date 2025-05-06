package final

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 03.
 */
class Page113 {
    suspend fun ex1() = coroutineScope {
//        launch {
            launch {
                delay(1000)
                throw Error("Some Error")
            }
            launch {
                delay(2000)
                println("will not be printed")
            }
            launch {
                delay(500)
                println("will be printed")
            }
//        }
    }
}

suspend fun main() {
    with(Page113()){
        ex1()
    }
}