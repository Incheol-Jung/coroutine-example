package final

import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume

interface Cancellable {
    fun cancel()
}

suspend fun requestNews(): News {
    return suspendCancellableCoroutine { cont ->
        val call = requestNewsApi { news ->
            cont.resume(news)
        }
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}


fun requestNewsApi(callback: (News) -> Unit): Cancellable {
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            // 가짜 뉴스 데이터 생성
            val news = News(title = "Breaking News", content = "This is the content of the breaking news.")
            callback(news)
        }
    }, 2000) // 2초 후에 콜백 호출

    return object : Cancellable {
        override fun cancel() {
            timer.cancel()
        }
    }
}
data class News(val title: String, val content: String)

// 코루틴 예제
fun main() = runBlocking {
    val job = launch {
        try {
            val news = requestNews()
            println("News Title: ${news.title}")
            println("News Content: ${news.content}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    delay(1000) // 1초 대기
    job.join() // 요청 종료
}

