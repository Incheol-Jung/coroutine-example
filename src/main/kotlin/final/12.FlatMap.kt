package final

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 29.
 */

class MapTest {
    val api = TempApi()
    suspend fun getOffers(
        categories: List<Category>
    ): List<Offer> = coroutineScope {
        categories
            .map { async { api.requestOffers(it) } }
            .flatMap { it.await() }
    }
}

class TempApi {
    suspend fun requestOffers(category: Category): List<Offer> {
        delay(2000)
        return listOf(Offer(id = 1, category = category))
    }
}

suspend fun main() {
    val times = measureTimedValue {
        val mapTest = MapTest()
        val offers = mapTest.getOffers(
            listOf(
                Category(id = 1, name = "Bobby Zamora"),
                Category(id = 2, name = "Marcos Castaneda"),
                Category(id = 3, name = "Gracie Gates")
            )
        )
        println(offers)
    }
    println(times)
}
