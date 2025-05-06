package final//import kotlinx.coroutines.suspendCancellableCoroutine
//
///**
// *
// * @author Incheol.Jung
// * @since 2024. 05. 12.
// */
//class NewCallback {
//    suspend fun requestNews(): News {
//        return suspendCancellableCoroutine<News> { cont ->
//            val call = requestNewsApi { news ->
//                cont.resume(news)
//            }
//            cont.invokeOnCancellation {
//                call.cancel()
//            }
//        }
//    }
//
//    suspend fun requestNews(): Result<News> {
//        return suspendCancellableCoroutine<News> { cont ->
//            val call = requestNewsApi(
//                onSuccess = { news ->
//                    cont.resume(Result.success(news))
//                },
//                onError = { e ->
//                    cont.resume(Result.failure(e))
//                }
//            )
//            cont.invokeOnCancellation {
//                call.cancel()
//            }
//        }
//    }
//}