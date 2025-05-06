package final

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 28.
 */

suspend fun askMultipleForData2(): String = coroutineScope {
    select {
        async { requestData1() }.onAwait { it }
        async { requestData2() }.onAwait { it }
    }
    // .also { coroutineContext.cancelChildren() }
}

suspend fun main(): Unit = coroutineScope {
    println(askMultipleForData2())
}
// (100 sec)
// Data2
