import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 12.
 */
interface SampleApi {
    @GET("/{organization}/hitsSingleSeason")
    suspend fun getBaseBallInfos(
        @Path("organization") organization: String
    ): List<BaseBallInfo>
}

data class BaseBallInfo(
    val Rank: String,
    val Player: String,
    val AgeThatYear: String,
    val Hits: Long,
    val Year: Long,
    val Bats: String,
    val id: Long,
)

fun main(): Unit = runBlocking {
    var gson = GsonBuilder().setLenient().create()
    // https://sampleapis.com/api-list/baseball(샘플 API 참고)
    val retrofit =
        Retrofit.Builder().baseUrl("https://api.sampleapis.com").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    val sampleApi = retrofit.create(SampleApi::class.java)
    val baseBallInfos = sampleApi.getBaseBallInfos("baseball")
    println(baseBallInfos)
}