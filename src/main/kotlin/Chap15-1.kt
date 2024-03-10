import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.TestCoroutineScheduler

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 09.
 */
class Temp(
    private val repo: UserDataRepository
) {
    suspend fun productionCurrentUserSeq(): UserTemp {
        val profile = repo.getProfile()
        val friends = repo.getFriends()
        return UserTemp(profile = profile, friends = friends)
    }

    suspend fun produceCurrentUserSym(): UserTemp = coroutineScope {
        val profile = async { repo.getProfile() }
        val friends = async { repo.getFriends() }
        UserTemp(profile = profile.await(), friends = friends.await())
    }


}

fun main() {
    val scheduler = TestCoroutineScheduler()
    println(scheduler.currentTime)
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime)
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime)
}
data class UserTemp(
    val profile: String,
    val friends: String,
)