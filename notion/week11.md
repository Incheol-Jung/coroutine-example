# 데이터/어댑터 계층

## 네트워크 요청을 다룰 때 사용

### 🙂 Retrofit

네트워크 요청을 정의한 함수를 블로킹 함수 대신 중단 함수로 만들고 싶다면 suspend 제어자를 추가하면 된다

```jsx
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
```

## 콜백 함수

- 코틀린 코루틴을 지원하지 않는 라이브러리를 사용해 콜백 함수를 반드시 사용해야 하는 경우라면, suspendCancellableCoroutine을 사용해 콜백 함수를 중단 함수로 변환할 수 있다
- 구현방법
    - Continuation 활용
        
        ```jsx
        suspend fun requestNews(): News {
                return suspendCancellableCoroutine<News> { cont ->
                    val call = requestNewsApi { news ->
                        cont.resume(news)
                    }
                    cont.invokeOnCancellation {
                        call.cancel()
                    }
                }
            }
        ```
        
        - 성공시 Continuation 객체의 resume 메서드를 사용해 코루틴을 재개해야 한다
        - 실패시 취소 가능하다면 invokeOnCancellation 람다식 내부에서 취소해야 한다
    - Result 객체 활용
        
        ```jsx
        suspend fun requestNews(): Result<News> {
                return suspendCancellableCoroutine<News> { cont ->
                    val call = requestNewsApi(
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
        ```
        
        - 콜백 함수를 래핑하고 Result를 반환 타입으로 설정한 뒤 코루틴을 Result.success 또는 Result.failure로 재개하는 것이 한 가지 방법이다
    - 널이 가능한 값을 반환한 뒤, 결괏값 또는 null 값으로 코루틴을 재개한다
        
        ```jsx
        suspend fun requestNews(): News? {
            return suspendCancellableCoroutine<News> { cont ->
                val call = requestNewsApi(
                    onSuccess = { news -> cont.resume(news) },
                    onError = { e -> cont.resume(null) }
                )
                cont.invokeOnCancellation {
                    call.cancel()
                }
            }
        }
        ```
        
    - 콜백 함수가 성공했을 때 결괏값을 반환하고 실패했을 때 예외를 던지는 것이다
        
        ```jsx
        suspend fun requestNews(): News {
            return suspendCancellableCoroutine<News> { cont ->
                val call = requestNewsApi(
                    onSuccess = { news -> cont.resume(news) },
                    onError = { e -> cont.resumeWithException(e) }
                )
                cont.invokeOnCancellation {
                    call.cancel()
                }
            }
        }
        ```
        

## 블로킹 함수

- 일반적인 중단 함수에서는 블로킹 함수를 절대 호출해서는 안된다
- 코틀린 코루틴에서는 스레드를 정밀하게 사용하기 때문에, 스레드가 블로킹되면 심각한 문제를 불러일으킬 수 있기 때문이다
- 그럼에도 불구하고 블로킹 함수를 호출하려면 withContext를 사용해 디스패처를 명시해야 한다

```jsx
class DiscSaveRepository(
    private val discReader: DiscReader
) : SaveRepository {

    override suspend fun loadSave(name: String): SaveData =
        withContext(Dispatchers.IO) {
            discReader.read("save/$name")
        }
}
```

## 플로우로 감지하기

- 플로우를 만들 때는 callbackFlow(또는 channelFlow)를 사용한다
- 플로우 빌더의 끝에는 awaitClose를 반드시 넣어줘야 한다

```jsx
fun listenMessages(): Flow<List<Message>> = callbackFlow {
    socket.on("NewMessage") { args ->
        trySendBlocking(args.toMessage())
    }
    awaitClose()
}
```

- 플로우는 콜백 함수로 사용될 수 있으며, 콜백 함수가 여러 개의 값을 만들 때 사용해야 한다

```jsx
fun flowFrom(api: CallbackBasedApi): Flow<T> = callbackFlow {
    val callback = object : Callback {
        override fun onNextValue(value: T) {
            trySendBlocking(value)
        }
        override fun onApiError(cause: Throwable) {
            cancel(CancellationException("API Error", cause))
        }
        override fun onCompleted() = channel.close()
    }
    api.register(callback)
    awaitClose { api.unregister(callback) }
}
```

- 플로우 빌더에서 특정 디스패처가 필요하다면, 생성된 플로우에서 flowOn을 사용하면 된다

```jsx
fun fibonacciFlow(): Flow<BigDecimal> = flow {
    var a = BigDecimal.ZERO
    var b = BigDecimal.ONE
    emit(a)
    emit(b)
    while (true) {
        val temp = a
        a = b
        b += temp
        emit(b)
    }
}.flowOn(Dispatchers.Default)
```

## 도메인 계층

- 두 개의 프로세스를 병렬로 실행하려면 함수 본체를 coroutineScope로 래핑하고 내부에서 async 빌더를 사용해 각 프로세스를 비동기로 실행해야 한다

```jsx
suspend fun produceCurrentUserPar(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```

- 만약 컬렉션 처리 함수와 async를 함께 사용하면 리스트의 각 원소를 비동기로 처리할 수 있다
- 이때는 awaitAll 함수를 사용해 결과를 기다리는 것이 좋다

```jsx
// case 1. await 사용
suspend fun getArticlesForUser(
    userToken: String?,
): List<ArticleJson> = coroutineScope {
    val articles = async { articleRepository.getArticles() }
    val user = userService.getUser(userToken)
    articles.await()
        .filter { canSeeOnList(user, it) }
        .map { toArticleJson(it) }
}

// case 2. awaitAll 사용
suspend fun getOffers(
    categories: List<Category>
): List<Offer> = coroutineScope {
    categories
        .map { async { api.requestOffers(it) } }
        .awaitAll()
        .flatten()
}
```

- 동시성 호출 수를 제한하고 싶다면 처리율 제한기를 사용할 수 있다.
- 리스트를 Flow로 변환하고 동시에 호출하는 횟수 제한을 명시하는 concurrency 파라미터를 flatMapMerge와 함께 사용할 수 있다

```jsx
fun getOffers(
    categories: List<Category>
): Flow<List<Offer>> = categories
    .asFlow()
    .flatMapMerge(concurrency = 20) {
        suspend { api.requestOffers(it) }.asFlow()
        // or flow { emit(api.requestOffers(it)) }
    }
```

- 서로 독립적인 작업 여러 개를 동시에 시작하고 싶다면, 자식 코루틴으로 예외전파가 되지 않는 supervisorScope를 사용해야 한다

```jsx
suspend fun notifyAnalytics(actions: List<UserAction>) =
    supervisorScope {
        actions.forEach { action ->
            launch {
                notifyAnalytics(action)
            }
        }
    }
```

## 플로우 변환

- 도메인 객체에서 코루틴 사용하는 방법을 끝내기 전에, 플로우를 처리하는 일반적인 방법을 살펴보자

```jsx
class UserStateProvider(
    private val userRepository: UserRepository
) {

    fun userStateFlow(): Flow<User> = userRepository
        .observeUserChanges()
        .filter { it.isSignificantChange }
        .scan(userRepository.currentUser()) { user, update ->
            user.with(update)
        }
        .map { it.toDomainUser() }
}
```

- 하나의 플로우를 여러 개의 코루틴이 감지하길 원한다면 SharedFlow로 변환해야 한다
- 스코프에서 shareIn을 사용하여 변환하는 방법이 가장 쉽다
- 필요한 경우에만 플로우를 액티브 상태로 유지하려면 stated 인자에 WhileSubscribed를 넣어준다

```jsx
class LocationService(
    locationDao: LocationDao,
    scope: CoroutineScope
) {
    private val locations = locationDao.observeLocations()
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
        )

    fun observeLocations(): Flow<List<Location>> = locations
}
```

# 표현/API/UI 계층

- 웹플럭스를 스프링 부트와 함께 사용할 경우, 컨트롤러 함수에 suspend 제어자만 추가하면 스프링은 함수를 코루틴으로 실행한다

```jsx
@Controller
class UserController(
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    @GetMapping("/me")
    suspend fun findUser(
        @PathVariable userId: String,
        @RequestHeader("Authorization") authorization: String
    ): UserJson {
        val userId = tokenService.readUserId(authorization)
        val user = userService.findUserById(userId)
        return user.toJson()
    }
}
```

## runBlocking 사용하기

- runBlocking을 사용하는 두 가지 목적은 다음과 같다
    1. main 함수를 포장하기 위해서 → coroutineScope로 대체 가능
    2. 테스트 함수를 포장하기 위해서 → runTest로 대체 가능
- 위에서 말한 목적이 아니라면 runBlocking을 사용해서는 안 된다
- runBlocking은 현재 스레드를 블로킹하며, 코틀린 코루틴에서 절대 일어나서는 안 되는 경우이다

# 코루틴 활용 비법

## 1. 비동기 맵

```jsx
suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }
        .awaitAll()
}

// 실제 사용 예
suspend fun getBestStudent(
	semester: String,
	repo: StudentsRepository
): Student = 
	repo.getStudentIds(semester)
			.mapAsync { repo.getStudent(it) }
			.maxBy { it.result }
			
// 실제 사용 예
suspend fun getCources(user: User): List<UserCourse> = 
	courseRepository.getAllCourses()
		.mapAsync { composeUserCourse(user, it) }
		.filterNot { courseShhouldBeHidden(user, it) }
		.sortedBy { it.state.ordinal }
```

- mapAsync 함수 덕분에 map, awaitAll, coroutineScope를 추상화하여 사용하지 않아도 된다.
- mapAsync 함수로 비동기 매핑을 좀더 명확하고 정확하게 구현할 수 있다
- 처리율 제한을 구현하여 동시에 들어오는 요청 수를 조절하고 싶으면 세마포어를 활용할 수 있다

```jsx
suspend fun <T, R> Iterable<T>.mapAsync(
		concurrencyLimit: Int = Int.MAX_VALUE,
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
		val semaphore = Semaphore(concurrencyLimit)
    this@mapAsync.map { 
	    async { 
		    semaphore.withPermit { 
			    transformation(it) 
		    }
	    } 
    }.awaitAll()
}
```

## 2. 지연 초기화 중단

- 코틀린 코루틴에서는 중단 함수를 map과 같은 suspend 제어자가 없는 람다식에서도 사용할 수 있다
- 람다식이 인라인 함수라면 suspend 제어자가 없어도 중단함수를 호출할 수 있으며, map은 인라인 함수이다

```jsx
suspend fun getOffers(
    categories: List<Category>
): List<Offer> = coroutineScope {
    categories
        .map { async { api.requestOffers(it) } }
        .flatMap { it.await() }
}
```

## 3. 연결 재사용

- 영구적인 HTTP 연결을 필요로 하거나 데이터베이스를 감지할 때 필요하다
- 연결을 유지하는 건 많은 비용이 들기 때문에, 같은 데이터를 받을 때 두 개의 연결을 유지할 필요가 없다
- 따라서 하나의 연결을 재사용하기 위해 플로우를 공유 플로우로 변환하는 방법을 알아보자

```jsx
class LocationService(
    locationDao: LocationDao,
    scope: CoroutineScope
) {
    private val locations = locationDao.observeLocations()
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
        )

    fun observeLocations(): Flow<List<Location>> = locations
}

class ConnectionPool<K, V>(
    private val scope: CoroutineScope,
    private val builder: (K) -> Flow<V>
) {

    private val connections = mutableMapOf<K, Flow<V>>()

    fun getConnection(key: K): Flow<V> = synchronized(this) {
        connections.getOrPut(key) {
            builder(key).shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed()
            )
        }
    }
}
```

- 실제 사용 예를 살펴보자

```jsx
private val scope = CoroutineScope(SupervisorJob())
private val messageConnections = 
	ConnectionPool(scope) { threadId: String ->
		api.observeMessageThread(threadId)
	}

fun observeMessageThread(threadId: String) = 
	messageConnections.getConnection(threadId)
```

- whileSubscribed를 사용했기 때문에 최소 하나의 코루틴이 연결을 사용하고 있을 때만 연결이 유지된다

## 4. 코루틴 경합

- 중단 가능한 프로세스 여러 개를 시작하고 먼저 끝나는 것의 결과를 기다리려면,                                         Splitties 라이브러리의 raceOf 함수를 사용하거나 직접 구현해도 된다

```jsx
suspend fun <T> raceOf(
    racer: suspend CoroutineScope.() -> T,
    vararg racers: suspend CoroutineScope.() -> T
): T = coroutineScope {
    select {
        (listOf(racer) + racers).forEach { racer ->
            async { racer() }.onAwait {
                coroutineContext.job.cancelChildren()
                it
            }
        }
    }
}

suspend fun fetchUserData(): UserData = raceOf(
	{ service1.fetchUserData() },
	{ service2.fetchUserData() }
)
```

## 5. 중단 가능한 프로세스 재시작하기

- 프로세스가 실패했을 경우 자동으로 다시 요청하도록 구현할수 있다
- 일전에 플로우에서 retry 또는 retryWhen 메서드를 통해서 재시도할 수 있는것을 살펴봤다
- 일반적인 중단 가능 프로세스를 재시도하는 함수는 없지만, 가장 간단한 방법으로 성공할 때까지 프로세스를 재시도하는 반복문을 만들 수 있다

```jsx
inline fun <T> retry(operation: () -> T): T {
	while (true) {
		try {
			return operation()
		} catch (e: Throwable) {
			// 처리안함
		}
	}
}

// 사용 예
suspend fun requestData(): String {
	if (Random.nextInt(0, 10) == 0) {
		return "ABC"
	} else {
		error("Error")
	}
}

suspend fun main(): Unit = coroutineScope {
	println(retry { requestData() })
}
// (1초 후)
// ABC
```

- 위의 예제는 실제 사용하기에는 부족한 부분이 있다
- 재시도하는 과정에 표준이 없다는 것이다. 재시도를 구현한다면, 다음과 같은 것을 추가하고 싶을 것이다
    - 재시고 횟수와 예외 종류에 따라 프로세스가 재시도되는 조건
    - 재시도 사이의 시간 간격 증가
    - 예외와 그 외 정보 로깅
- retry를 구현하는 두 가지 좋은 방법이 있다

### retryWhen

- retryWhen처럼 사용자 측면에서 파라미터화하기 쉬운 범용 함수를 정의하는 것이다
- 하지만 이는 범용적으로 제공하기 위해 다음 두 가지 특징을 가진다
    - 취소 과정에 영향을 끼치지 않기 위해 취소 예외는 재시도하지 않는다
    - 이전에 발생한 예외는 무시된 예외로 판단하며, 마지막 예외를 함수 밖으로 던질 때 출력된다

```jsx
inline fun <T> retryWhen(
	predicate: (Throwable, retires: Int) -> Boolean,
	operation: () -> T
): T {
	var retries = 0
	var fromDownStream: Throwable? = null
	while (true) {
		try {
			return operation()
		} catch (e: Throwable) {
			if (fromDownStream != null) {
				e.addSuppressed(fromDownStream)
			}
			fromDownStream = e
			if (e is CancellationException || !predicate(e, retries++)) {
				throw e
			}
		}
}

// 사용 예
suspend fun requestWithRetry(attempt: Int) = retryWhen(
	predicate = { e, retries ->
		val times = 2.0.pow(attempt.toDouble()).toInt()
		delay(maxOf(10_000L, 100L * times))
		log.error(e) { "Retried" }
		retries < 10 && e is IllegalStateException
	}
) {
	requestData()
}

suspend fun main(): Unit = coroutineScope {
    println(requestWithRetry(10))
}
```

### 애플리케이션 종속 retry 함수 구현

```jsx
inline suspend fun <T> retry(
	operation: () -> T
): T {
	var retires = 0
	while (true) {
		try {
			return operation()
		} catch (e: Exception) {
			val times = 2.0.pow(attempt.toDouble()).toInt()
			delay(maxOf(10_000L, 100L * times))
			if (e is CancellationException || retires >= 10) }
				throw e
			}
			retries++
			log.error(e) { "Retrying" }
		}
	}
}

// 사용 예
suspend fun requestWithRetry() = try {
	requestData()
}	
```
