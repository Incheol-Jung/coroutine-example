package final

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

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

fun main() = runBlocking {
    // 기본 Flow: 1부터 5까지의 정수를 생성
    val numberFlow = flow {
        for (i in 1..5) {
            emit(i)
            delay(500) // 0.5초 대기
        }
    }

    // callbackFlow를 사용하여 문자열을 생성
    fun callbackFlowExample(str: String): Flow<String> = callbackFlow {
        val sendData: (String) -> Unit = { data ->
            println("callback send $data")
            trySend(data)
        }

        val closeChannel: () -> Unit = {
            close()
        }

        // Simulate an async operation
        sendData(str)

        awaitClose {
            println("Close CallbackFlow")
        }
    }

    // callbackFlow 호출
    val stringFlow = callbackFlowExample("Hello, Flow!")

    // 두 개의 Flow를 결합
    val combinedFlow = channelFlow {
        launch {
            numberFlow.collect { value ->
                send("Number: $value")
            }
        }
        launch {
            stringFlow.collect { value ->
                send(value)
            }
        }
    }

    // 결합된 Flow를 수집하여 출력
    combinedFlow.collect { value ->
        println("Received: $value")
    }
}