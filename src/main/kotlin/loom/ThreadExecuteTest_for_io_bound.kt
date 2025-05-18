package loom

import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

/**
 *
 * @author Incheol.Jung
 * @since 2025. 05. 19.
 */
// 1. IO-bound 테스트

fun simulateIO() {
    Thread.sleep(100)
}

// 1-1. 플랫폼 스레드
//fun main() {
//    val numberOfTasks = 1000
//    val executor = Executors.newFixedThreadPool(8)
//
//    val time = measureTimeMillis {
//        val futures = (1..numberOfTasks).map {
//            executor.submit {
//                simulateIO()
//            }
//        }
//        futures.forEach { it.get() }
//    }
//
//    executor.shutdown()
//    println("CPU-bound: Regular Threads took ${time}ms")
//}

// 1-2. 가상 쓰레드
fun main() {
    val numberOfTasks = 1000
    val executor = Executors.newFixedThreadPool(8)

    val time = measureTimeMillis {
        val futures = (1..numberOfTasks).map {
            Thread.ofVirtual().start {
                simulateIO()
            }
        }
        futures.forEach { it.join() }
    }

    executor.shutdown()
    println("CPU-bound: Regular Threads took ${time}ms")
}
