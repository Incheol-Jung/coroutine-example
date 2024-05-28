package final

import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume

suspend fun requestNews3(): News? {
    return suspendCancellableCoroutine { cont ->
        val call = requestNewsApi3(
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resume(null) }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}

fun requestNewsApi3(
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
        try {
            val news = requestNews3()
            println("News Title: ${news?.title}")
            println("News Content: ${news?.content}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    delay(1000) // 1초 대기
    job.join() // 요청 취소
}