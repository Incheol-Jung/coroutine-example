package bible.chap1

/**
 *
 * @author Incheol.Jung
 * @since 2024. 11. 10.
 */
class ex2 {
}

fun main() {
    println("메인 스레드 시작")
    throw Exception("Dummy Exception")
    println("메인 스레드 종료")
}