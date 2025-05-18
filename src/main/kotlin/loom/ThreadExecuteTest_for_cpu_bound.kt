package loom

import java.util.concurrent.Executors
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

/**
 *
 * @author Incheol.Jung
 * @since 2025. 05. 19.
 */
// 1. CPU-bound 테스트
fun heavyCpuTask() {
    var sum = 0L
    for (i in 1..1_000_000) {
        sum += i * i
    }
}

// 1-1. 플랫폼 스레드
//fun main() {
//    val numberOfTasks = 10000000
//    val executor = Executors.newFixedThreadPool(8)
//
//    val time = measureTimeMillis {
//        val futures = (1..numberOfTasks).map {
//            executor.submit {
//                heavyCpuTask()
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
    val numberOfTasks = 10000000
    val executor = Executors.newFixedThreadPool(8)

    val time = measureTimeMillis {
        val futures = (1..numberOfTasks).map {
            Thread.ofVirtual().start {
                heavyCpuTask()
            }
        }
        futures.forEach { it.join() }
    }

    executor.shutdown()
    println("CPU-bound: Regular Threads took ${time}ms")
}