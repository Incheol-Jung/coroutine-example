package bible.chap1

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 10.
 */

class ex3{

}

class ExampleThread : Thread() {
    override fun run() {
        println("[${Thread.currentThread().name}] 새로운 스레드 시작")
        Thread.sleep(2000L) // 2초 동안 대기
        println("[${Thread.currentThread().name}] 새로운 스레드 종료")
    }
}

fun main() {
    println("[${Thread.currentThread().name}] 메인 스레드 시작")
    ExampleThread().start()
    Thread.sleep(100L)
    println("[${Thread.currentThread().name}] 메인 스레드 종료")
}

//fun main() {
//    println("[${Thread.currentThread().name}] 메인 스레드 시작")
//    ExampleThread().apply { isDaemon = true }.start()
//    Thread.sleep(100L)
//    println("[${Thread.currentThread().name}] 메인 스레드 종료")
//}