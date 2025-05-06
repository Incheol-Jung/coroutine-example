package final

import kotlinx.coroutines.*

/**
 *
 * @author Incheol.Jung
 * @since 2023. 10. 02.
 */
fun main(): Unit {
    runBlocking {
        a()
    }
}

suspend fun a() {
    val user = readUser()
    b()
    b()
    b()
    println(user)
}

suspend fun c(i: Int) {
    delay(i * 100L)
    println("Tick")
}

fun readUser(): String {
    return "test"
}

suspend fun b() {
    for (i in 1..10) {
        c(i)
    }
}