package final

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 19.
 */
var sendData: (data: Int) -> Unit = { }
var closeChannel: () -> Unit = { }

fun callbackFlow(str: String): Flow<String> = callbackFlow {
    sendData = { data ->
        println("callback send $data")
        trySend(str)
    }
    closeChannel = { close() }
    awaitClose {
        sendData = {}
        closeChannel = {}
        println("Close CallbackFlow")
    }
}

fun collectCallbackData() = CoroutineScope(Dispatchers.Default).launch {
    callbackFlow("temp").collect {
        println("callback collect $it")
    }
}

fun main(): Unit = runBlocking {

    println("Emit 1 before collect")
    sendData(1)

    println("Collect started")
    runBlocking {
        callbackFlow("temp").collect {
            println("callback collect $it")
        }
    }

}
