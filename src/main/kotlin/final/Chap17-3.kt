package final

import kotlinx.coroutines.coroutineScope
import recipes.raceOf

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 28.
 */

suspend fun askMultipleForData3(): String = raceOf(
    { requestData1() },
    { requestData2() }
)

suspend fun main(): Unit = coroutineScope {
    println(askMultipleForData3())
}
// (100 sec)
// Data2
