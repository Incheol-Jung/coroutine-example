import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 28.
 */
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
//            if (index == 3) {
//                throw RuntimeException("test")
//            }
        }
        channel.close()
    }

    launch {
        for (element in channel) {
            println(element)
        }
    }
}