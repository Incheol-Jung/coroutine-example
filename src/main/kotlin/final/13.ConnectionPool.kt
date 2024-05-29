package final

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 30.
 */
class ConnectionPool<K, V>(
    private val scope: CoroutineScope,
    private val builder: (K) -> Flow<V>
) {

    private val connections = mutableMapOf<K, Flow<V>>()

    fun getConnection(key: K): Flow<V> = synchronized(this) {
        connections.getOrPut(key) {
            builder(key).shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed()
            )
        }
    }
}

//fun main(){
//    val scope = CoroutineScope(SupervisorJob())
//    val messageConnections =
//        ConnectionPool(scope = scope) { threadId: String ->
//            observeMessageThread(threadId)
//        }
//    fun observeMessageThread(threadId: String) =
//    messageConnections.getConnection(threadId)
//
//}
