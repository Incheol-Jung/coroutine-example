import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 10.
 */
class TestTest2 {
    @Test
    fun test1() = runTest {
        var i = 0
        launch {
            while (true) {
                delay(1000)
                i++
            }
        }

        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i)
    }

    @Test
    fun test2() = runTest {
        var i = 0
        backgroundScope.launch {
            while (true) {
                delay(1000)
                i++
            }
        }

        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i)
    }

    suspend fun <T, R> Iterable<T>.mapAsync(
        transformation: suspend (T) -> R
    ): List<R> = coroutineScope {
        this@mapAsync.map { async { transformation(it) } }.awaitAll()
    }

    @Test
    fun test3() = runTest {
        var ctx: CoroutineContext? = null
        val name1 = CoroutineName("Name 1")
        withContext(name1) {
            listOf("A").mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name1, ctx?.get(CoroutineName))
        }

        val name2 = CoroutineName("Name 2")
        withContext(name2) {
            listOf(1,2,3).mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name2, ctx?.get(CoroutineName))
        }
    }


}