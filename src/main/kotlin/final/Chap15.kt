package final

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 09.
 */
class FetchUserUseCase(
    private val repo: UserDataRepository
) {
    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await(),
        )
    }
}

data class User(
    val name: String,
    val friends: String,
    val profile: String,
)

open class UserDataRepositoryImpl : UserDataRepository {
    override suspend fun getName(): String {
        return "name"
    }
    override suspend fun getFriends(): String {
        return "friends"
    }
    override suspend fun getProfile(): String {
        return "profile"
    }
}

interface UserDataRepository {
    suspend fun getName(): String
    suspend fun getFriends(): String
    suspend fun getProfile(): String
}
