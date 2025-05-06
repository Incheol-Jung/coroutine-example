package final

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.random.Random

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 27.
 */
suspend fun getOffers(
    categories: List<Category>
): List<Offer> = categories
    .asFlow()
    .flatMapMerge {
        suspend { requestOffer(it) }.asFlow()
    }.flowOn(Dispatchers.IO)
    .toList()

fun requestOffer(category: Category): Offer {
    return Offer(id = Random.nextInt(), category = category)
}

data class Category(var id: Number, val name: String){

}
data class Offer(val id: Number, val category: Category)

suspend fun main() {
    val offers = getOffers(
        categories = listOf(
            Category(id = 1, name = "Blaine Lott"),
            Category(id = 2, name = "Jimmy Hunter"),
            Category(id = 3, name = "Gonzalo Foley")
        )
    )

    offers.stream().forEach {
        println(it)
    }
}