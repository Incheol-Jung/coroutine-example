# 📌 인상 깊었던 내용

## **📚 stream에 대한 이해**

```jsx
List<Integer> list = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
List<Integer> filterList = list.stream()
	.filter(d -> {
		System.out.println("filtering" + d.toString());
		return d.intValue() < 5;
	})
	.map(d -> {
		System.out.println("mapping" + d.toString());
		return d;
	})
	.limit(3)
	.collect(Collectors.toList());
```

> stream 내용이 나와서 위의 코드는 어떻게 동작하며 print는 어떻게 찍힐까? 
그리고 그렇게 동작하는 원리는 무엇일까?
 
📕 254p 1번째 (18장)
> 

### **🧐 : 쇼트 서킷(short circuit)과 늦은 연산(lazy evaluation) 덕분에 해당 동작원리가 가능하다(java 8 in action 참고)**

# 📌 이해가 가지 않았던 내용

## **📚 핫 vs 콜드**

```jsx
// 채널
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

//sampleStart
private fun CoroutineScope.makeChannel() = produce {
    println("Channel started")
    for (i in 1..3) {
        delay(1000)
        send(i)
    }
}

suspend fun main() = coroutineScope {
    val channel = makeChannel()

    delay(1000)
    println("Calling channel...")
    for (value in channel) {
        println(value)
    }
    println("Consuming again...")
    for (value in channel) {
        println(value)
    }
}
// Channel started
// (1 sec)
// Calling channel...
// 1
// (1 sec)
// 2
// (1 sec)
// 3
// Consuming again...
//sampleEnd

// 플로우
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

//sampleStart
private fun makeFlow() = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

suspend fun main() = coroutineScope {
    val flow = makeFlow()

    delay(1000)
    println("Calling flow...")
    flow.collect { value -> println(value) }
    println("Consuming again...")
    flow.collect { value -> println(value) }
}
// (1 sec)
// Calling flow...
// Flow started
// (1 sec)
// 1
// (1 sec)
// 2
// (1 sec)
// 3
// Consuming again...
// Flow started
// (1 sec)
// 1
// (1 sec)
// 2
// (1 sec)
// 3
//sampleEnd
```

> 핫 데이터 스트림은 열정적이라 데이터를 소비하는 것과 무관하게 원소를 생성하지만, 콜드 데이터 스트림은 게을러서 요청이 있을 때만 작업을 수행하며 아무것도 저장하지 않는다
컬렉션은 핫이며, Sequence와 자바의 Stream은 콜드이다.
Channel은 핫이지만 Flow와 Rxjava 스트림은 콜드이다
그런데 채널과 플로우의 핫/콜드 개념은 조금 다르다
채널은 핫 데이터 스트림이기 때문에 첫 번째 수신자가 모든 원소를 소비하고 나면 두 번째 소비자는 채널이 비어있으며 이미 닫혀 있다는걸 발견하게 된다. 
플로우의 각 최종 연산은 처음부터 데이터를 처리하기 시작한다
 
📕 252p 3번째 (18장)
> 

### **🧐 : 내가 알고 있는 리액티브 프로그래밍에서 사용하는 콜드 시퀀스와 핫 시퀀스의 개념은 반대로 알고 있는데 몬가 혼동된다..ㅜ
(참고 :** https://baekjungho.github.io/wiki/reactive/reactive-hot-cold/)

# 📌 논의해보고 싶었던 내용
