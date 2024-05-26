package final

import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume

suspend fun requestNews2(): Result<News> {
    return suspendCancellableCoroutine { cont ->
        val call = requestNewsApi2(
            onSuccess = { news ->
                cont.resume(Result.success(news))
            },
            onError = { e ->
                cont.resume(Result.failure(e))
            }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}

fun requestNewsApi2(
    onSuccess: (News) -> Unit,
    onError: (Exception) -> Unit
): Cancellable {
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            if (Math.random() > 0.5) {
                // 성공 콜백 호출
                val news = News(title = "Breaking News", content = "This is the content of the breaking news.")
                onSuccess(news)
            } else {
                // 실패 콜백 호출
                onError(Exception("Failed to fetch news"))
            }
        }
    }, 2000) // 2초 후에 콜백 호출

    return object : Cancellable {
        override fun cancel() {
            timer.cancel()
        }
    }
}

// 코루틴 예제
fun main() = runBlocking {
    val job = launch {
        val result = requestNews2()
        result.fold(
            onSuccess = { news ->
                println("News Title: ${news.title}")
                println("News Content: ${news.content}")
            },
            onFailure = { e ->
                println("Error: ${e.message}")
            }
        )
    }

    delay(1000) // 1초 대기
    job.join() // 요청 취소
}
