import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 09.
 */
class FetchUserUseCaseTest {
    @Test
    fun fetchUserData() = runBlocking {
        // given
        val repo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "name",
            friends = "friends",
            profile = "profile"
        )
        assertEquals(result, expectedUser)
    }
}

class FakeUserDataRepository : UserDataRepository {
    override suspend fun getName(): String = "name"
    override suspend fun getFriends(): String = "friends"
    override suspend fun getProfile(): String = "profile"
}