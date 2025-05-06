package final

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
///**
// *
// * @author Incheol.Jung
// * @since 2024. 03. 28.
// */
suspend fun requestData1(): String {
    delay(100000)
    return "Data1"
}

suspend fun requestData2(): String {
    delay(10)
    return "Data2"
}

val scope = CoroutineScope(SupervisorJob())

suspend fun askMultipleForData(): String {
    val defData1 = scope.async { requestData1() }
    val defData2 = scope.async { requestData2() }
    return select {
        defData1.onAwait { it }
        defData2.onAwait { it }
    }
}

suspend fun main(): Unit = coroutineScope {
    println(askMultipleForData())
}
//// (1 sec)
//// Data2
