package bible.chap1

import java.util.concurrent.Executors

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 16.
 */
class ex5 {
}

fun main() {
    val executorService = Executors.newFixedThreadPool(2)
    val future = executorService.submit<String> {
        Thread.sleep(2000)
        "작업1완료"
    }

    val result = future.get() // 메인 스레드가 블로킹 됨
    println(result)
    executorService.shutdown()
}