package loom

import bible.chap1.getElapsedTime
import java.util.stream.IntStream

/**
 *
 * @author Incheol.Jung
 * @since 2025. 05. 06.
 */

// 플랫폼 쓰레드 활용
fun main() {
    val startTime = System.currentTimeMillis()
    println("[${getElapsedTime(startTime)}] 작업 시작")
    val threads = IntStream
        .range(0, 1_000_000)
        .mapToObj { i -> Thread { println(i) } }.toList()

    threads.forEach(Thread::start)
    println("[${getElapsedTime(startTime)}] 작업 완료")
}

// 가상 쓰레드 활용
//fun main() {
//    val startTime = System.currentTimeMillis()
//    println("[${getElapsedTime(startTime)}] 작업 시작")
//
//    val threads = IntStream
//        .range(0, 1_000_000)
//        .mapToObj { i -> Thread.ofVirtual().unstarted { println(i) } }.toList()
//    threads.forEach(Thread::start)
//
//    println("[${getElapsedTime(startTime)}] 작업 완료")
//}

