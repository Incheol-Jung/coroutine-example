package final

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 * @author Incheol.Jung
 * @since 2023. 10. 02.
 */
fun main() = runBlocking {
    println("Before")

//    val temp = async {
        suspendCoroutine<Unit> { continuation ->
            thread {
                println("Suspended on ${Thread.currentThread()}")
                Thread.sleep(2000)
                continuation.resume(Unit)
                println("Running on ${Thread.currentThread()}")
            }
        }
//    }

    println("temp1 on ${Thread.currentThread()}")
//    Boolean.
//    temp.await()
    println("After on ${Thread.currentThread()}")
//    printWithThread("START")
//    launch {
//        newRoutine()
//    }
//    printWithThread("END")
//    repeat(1000000) {
//        thread {
//            Thread.sleep(1000L)
//            println(".")
//        }
//    }
}

suspend fun myFunction() {
    println("Before")
    delay(1000L) // 중단 함수
    println("After")
}

suspend fun getTemp(): Number {
    return 1
}

fun newRoutine() {
    val num1 = 1
    val num2 = 2
    printWithThread("$num1+$num2")
}

fun printWithThread(str : String) {
    println("[${Thread.currentThread().name}] $str")
}
