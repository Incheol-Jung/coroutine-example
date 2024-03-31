import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 28.
 */
suspend fun main(): Unit = coroutineScope {
    val channel = produce (capacity = Channel.UNLIMITED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println(index)
        }
    }

    delay(1000)
    launch {
        for (element in channel) {
            println(element)
        }
    }
}