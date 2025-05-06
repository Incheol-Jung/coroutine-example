package bible.chap1

import java.util.concurrent.Executors

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 10.
 */
class ex4 {
}

fun main() {
    val startTime = System.currentTimeMillis()
    // ExecutorService 생성
    val executorService = Executors.newFixedThreadPool(2)

    // 작업 1 제출
    executorService.submit{
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 1 시작")
        Thread.sleep(1000L)
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 1 완료")
    }

    // 작업 2 제출
    executorService.submit{
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 2 시작")
        Thread.sleep(1000L)
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 2 완료")
    }

    // 작업 3 제출
    executorService.submit{
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 3 시작")
        Thread.sleep(1000L)
        println("[${Thread.currentThread().name}][${getElapsedTime(startTime)}] 작업 3 완료")
    }

    executorService.shutdown()
}

fun getElapsedTime(startTime: Long): String =
    "지난 시간: ${System.currentTimeMillis() - startTime}ms"