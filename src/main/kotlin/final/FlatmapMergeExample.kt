package final

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 27.
 */
fun getOffers(
    categories: List<Category>
): Flow<Offer> = categories
    .asFlow()
    .flatMapMerge(concurrency = 20) {
        suspend { requestOffer(it) }.asFlow()
    }

fun requestOffer(category: Category): Offer {
    return Offer(id = Random.nextInt(), category = category)
}

data class Category(val id: Number, val name: String)
data class Offer(val id: Number, val category: Category)

suspend fun main() {
    val offers = getOffers(
        listOf(
            Category(id = 1, name = "Blaine Lott"),
            Category(id = 2, name = "Jimmy Hunter"),
            Category(id = 3, name = "Gonzalo Foley")
        )
    )

    offers.collect {
        println(it)
    }
}