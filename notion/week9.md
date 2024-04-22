# 📌 인상 깊었던 내용

## **📚 merge, zip, combine**

> 두개의 플로우를 합치는 의미는 동일하나 각각의 차이에 대해서 숙지하자
merge - 한 플로우의 원소가 다른 플로우를 기다리지 않는다는 것이 중요하다
zip - 각 원소는 한 쌍의 일부가 되므로 쌍이 될 원소를 기다려야 한다. 쌍을 이루지 못하고 남은 원소는 유실되므로 한 플로우에서 지핑이 안료되면 생성되는 플로우 또한 완료된다
combine - zip처럼 원소들로 쌍을 만들기 위해 느린 플로우를 기다려야 한다. zip과 다르게 combine은 그런 제한이 없기 때문에 두 플로우 모두 닫힐 때까지 원소를 내보낸다

📕 311p 4번째 (23장)
> 

### **🧐 : merge는 바로바로 소비되고, zip은 쌍이 될때 소비되고, combine은 쌍이되도 기존의 플로우의 데이터를 재사용해서 소비한다**

## **📚 catch는 최종연산에선 사용할 수 없다**

```jsx
val flow = flow {
	emit("Message1")
	emit("Message2")
}

suspend fun main(): Unit {
	flow.onStart { println("Before") }
			.catch { println("Caught %it") }
			.collect { throw MyError) }
}
// Before
// Exception in thread "..." MyError: My error

suspend fun main(): Unit {
	try {
		flow.collect { println("Collected $it") }
	} catch (e: MyError) {
		println("Catght")
	}
}
// Collected Message1
// Caught
```

> catch를 사용하는 건 (마지막 연산 뒤에 catch가 올 수 없기 때문에) 최종 연산에서 발생한 예외를 처리하는 데 전혀 도움이 되지 않는다. 
그러므로 collect의 연산을 onEach로 옮기고 catch 이전에 두는 방법이 자주 사용된다

📕 301p 1번째 (22장)
> 

### **🧐 : 예외처리시 catch를 사용해서는 최종 연산자는 사용할 수 없으므로 중간 연산자에 로직을 넣거나 외부에 try-catch로 감싸는 방법밖에 없을것 같다. 나중에 참고하기 위해 기록**

## **📚  flowOn**

```jsx
suspend fun present(place: String, message: String) {
	val ctx = coroutineContext
	val name = ctx[CoroutineName]?.name
	println("[$name] $message on $place")
}

fun messagesFlow(): Flow<String> = flow {
	present("flow builder", "Message")
	emit("Message")
}

suspend fun main() {
	val users = messagesFlow()
	withContext(CoroutineName("Name1")) {
		users
			.flowOn(CoroutineName("Name3"))
			.onEach { present("onEach", it) }
			.flowOn(CoroutineName("Name2"))
			.collect { present("collect", it) }
	}
}

// [Name3] Message on flow builder
// [Name2] Message on onEach
// [Name1] Message on collect
```

> flowOn 함수로 컨텍스트를 변경할 수도 있습니다

📕 302p 9번째 (22장)
> 

### **🧐 : 컨텍스트를 수정해서 flow 로직을 좀더 리소스를 유용하게 사용할 수 있을것 같아서 메모!**

# 📌 이해가 가지 않았던 내용

## **📚 onStart**

```jsx
suspend fun main() {
	flowOf(1, 2)
		.onEach { delay(1000) }
		.onStart { println("Before") }
//		.onStart { emit(0) }
		.collect { println(it) }
}
// Before
// (1초 후)
// 1
// (1초 후)
// 2
```

> onStart 함수는 최종 연산이 호출될 때와 같이 플로우가 시작되는 경우에 호출되는 리스너를 설정합니다. onStart는 첫 번째 원소가 생성되는 걸 기다렸다 호출되는 게 아니라는 것이 중요합니다. 첫 번째 원소를 요청했을 때 호출되는 함수입니다.

📕 296p 1번째 (22장)
> 

### **🧐 : 최초에 원소 소비했을때 추가적으로 작업해야 하는 케이스가 있을까..? 딱히 떠오르지가 않는다..**

# 📌 논의해보고 싶었던 내용
