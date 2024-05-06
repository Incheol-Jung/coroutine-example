# 📌 인상 깊었던 내용

## **📚 리스너 옵션**

```jsx
suspend fun main(): Unit = coroutineScope {
    val flow = flowOf("A", "B", "C")
        .onEach { delay(1000) }

    val sharedFlow: SharedFlow<String> = flow.shareIn(
        scope = this,
        started = SharingStarted.Eagerly,
        // replay = 0 (default)
    )

    delay(500)

    launch {
        sharedFlow.collect { println("#1 $it") }
    }

    delay(1000)

    launch {
        sharedFlow.collect { println("#2 $it") }
    }

    delay(1000)

    launch {
        sharedFlow.collect { println("#3 $it") }
    }
}
// (1 sec)
// #1 A
// (1 sec)
// #1 B
// #2 B
// (1 sec)
// #1 C
// #2 C
// #3 C

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val flow1 = flowOf("A", "B", "C")
    val flow2 = flowOf("D")
        .onEach { delay(1000) }

    val sharedFlow = merge(flow1, flow2).shareIn(
        scope = this,
        started = SharingStarted.Lazily,
    )

    delay(100)
    launch {
        sharedFlow.collect { println("#1 $it") }
    }
    delay(1000)
    launch {
        sharedFlow.collect { println("#2 $it") }
    }
}
// (0.1 sec)
// #1 A
// #1 B
// #1 C
// (1 sec)
// #2 D
// #1 D
//sampleEnd

suspend fun main(): Unit = coroutineScope {
    val flow = flowOf("A", "B", "C", "D")
        .onStart { println("Started") }
        .onCompletion { println("Finished") }
        .onEach { delay(1000) }

    val sharedFlow = flow.shareIn(
        scope = this,
        started = SharingStarted.WhileSubscribed(),
    )

    delay(3000)
    launch {
        println("#1 ${sharedFlow.first()}")
    }
    launch {
        println("#2 ${sharedFlow.take(2).toList()}")
    }
    delay(3000)
    launch {
        println("#3 ${sharedFlow.first()}")
    }
}
// (3 sec)
// Started
// (1 sec)
// #1 A
// (1 sec)
// #2 [A, B]
// Finished
// (1 sec)
// Started
// (1 sec)
// #3 A
// Finished
```

> 리스너의 수에 따라 값을 언제부터 감지할지 결정된다
- SharingStated.Eagerly: 즉시 값을 감지하기 시작하고 플로우로 값을 전송한다. 
- SharingStarted.Lazily: 첫 번째 구독자가 나올 때 감지하기 시작한다. 첫번째 구독자는 내보내진 모든 값을 수신하는 것이 보장되며, 이후의 구독자는 replay 수만큼 가장 퇴근에 저장된 값들을 받게 된다. 
- WhileSubscribed() : 첫 번째 구독자가 나올 때 감지하기 시작하며, 마지막 구독자가 사라지면 플로우도 멈춘다

📕 336p 1번째 (24장)
> 

### **🧐 : 리스너 옵션 숙지해보자**

# 📌 이해가 가지 않았던 내용

## **📚 플로우 테스트 하기**

```jsx
class MessagesServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
        // given
        val source = flow {
            emit(Message(fromUserId = "0", text = "A"))
            delay(1000)
            emit(Message(fromUserId = "1", text = "B"))
            emit(Message(fromUserId = "0", text = "C"))
        }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )

        // when
        val emittedMessages = mutableListOf<Message>()
        service.observeMessages("0") // 아 여기 filter 기능이 있었군..
            .onEach { emittedMessages.add(it) }
            .launchIn(backgroundScope)
        delay(1)

        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
            ), emittedMessages
        )

        // when
        delay(1000)

        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"), // 여기에 B도 있어야 하는데...
                Message(fromUserId = "0", text = "C"), 
            ), emittedMessages
        )
    }
}
```

> 다음 방법은 backgroundScope에서 플로우를 시작하고 플로우가 방출하는 모든 원소를 컬렉션에 저장하는 것이다. 이러한 방식은 실패하는 경우에 ‘무엇인지’와 ‘무엇이 되어야 하는지’에 대해 명확하게 보여 줄 뿐만 아니라 테스트 시간을 유연하게 설정할 수 있게 해줍니다 → 이해되었음.. observeMessages 함수에서 필터링함!!

📕 354p 1번째 (25장)
> 

### **🧐 : 목록에는 text 값이 ‘B’인 flow도 있었는데 어느샌가 사라졌다… 왜지..??** → 이해되었음.. observeMessages 함수에서 필터링함!!

# 📌 논의해보고 싶었던 내용
