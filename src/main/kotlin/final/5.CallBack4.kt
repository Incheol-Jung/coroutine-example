package final

import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun requestNews4(): News {
    return suspendCancellableCoroutine { cont ->
        val call = requestNewsApi2(
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resumeWithException(e) }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}

// 코루틴 예제
fun main() = runBlocking {
    val job = launch {
        try {
            val news = requestNews4()
            println("News Title: ${news.title}")
            println("News Content: ${news.content}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    delay(1000) // 1초 대기
    job.join() // 요청 취소
}