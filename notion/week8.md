# 📌 인상 깊었 던 내용

## **📚 코틀린 함수형 인터페이스**

```jsx
fun interface FlowCollector {
	suspend fun emit(value: String)
}

suspend fun main() {
    val f: suspend (FlowCollector) -> Unit = {
        it.emit("A")
        it.emit("B")
        it.emit("C")
    }

    val g: suspend FlowCollector.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    
    f { print(it) } // ABC
    f { print(it) } // ABC

    g { print(it) } // ABC
    g { print(it) } // ABC
}
```

> FlowCollector 함수형 인터페이스를 정의해 간단하게 만들어 보자
 
📕 273p 1번째 (20장)
> 

### **🧐 :** 코루틴과는 관련이 없지만 사실 함수형 인터페이스를 실무에서 많이 사용해봐야지 생각만 하고 있었는데, 
코틀린에서는 함수형 인터페이스를 이렇게 구현할수 있구나 다시금 알게되면서 올해에는 꼭 사용해봐야지 생각되서 적어봅니다

## **📚 함수를 플로우로 바꾸기**

```jsx
suspend fun main() {
	val function = suspend {
		// 중단 함수를 람다식으로 만든 것이다
		delay(1000)
		"UserName"
	}
	
	function.asFlow()
		.collect { println(it) }
}
```

> 중단 함수를 플로우로 변환하는 것 또 한 가능하다
 
📕 284p 5번째 (21장)
> 

### **🧐 : 함수를 플로우로 변환해서 사용하는건 실무에서도 사용할것 같아서 메모해본다**

# 📌 이해가 가지 않았던 내용

## **📚 Flow 실제 구현**

```jsx
fun Flow<*>.counter(): Flow<Int> {
    var counter = 0
    return this.map {
        counter++
        // to make it busy for a while
        List(100) { Random.nextLong() }.shuffled().sorted()
        // emit(counter)
        counter
    }
}

suspend fun main(): Unit = coroutineScope {
    val f1 = List(1_000) { "$it" }.asFlow()
    val f2 = List(1_000) { "$it" }.asFlow()
        .counter()

    launch { println(f1.counter().last()) } // 1000
    launch { println(f1.counter().last()) } // 1000
    launch { println(f2.last()) } // less than 2000
    launch { println(f2.last()) } // less than 2000
}
```

> 외부 변수는 같은 플로우가 모으는 모든 코루틴이 공유하게 된다. 이런 경우 동기화가 필수이며 플로우 컬렉션이 아니라 플로우에 종속되게 된다. 따라서 두 개의 코루틴이 병렬로 원소를 세게 되고, f2.last()는 1000이 아니라 2000을 반환하게 된다
 
📕 280p 1번째 (20장)
> 

### **🧐 : emit으로 호출하면 동기화가 안되는데, counter를 그대로 리턴하면 공유된다..? 흠.. 차이가 이해되지 않는다.. 😭**

# 📌 논의해보고 싶었던 내용

## **📚 flow vs channelflow vs callbackFlow**

> flow : flow는 콜드 데이터 스트림이므로 필요할 때만 값을 생성
channelflow : 데이터를 생성하고 소비하는 과정을 별개로 진행하고 싶을때 동시성을 보장하기 위함
callbackflow : 감지하는 프로세스는 이벤트를 처리하는 프로세스와 독립적이어야 하므로 channelFlow를 사용해도 좋다. 하지만 이 경우에 callbackFlow를 사용하는 것이 더 낫다(콜백을 사용할 때 에러에 덜 민감하도록 몇가지 작은 변화가 있다)

📕 289p 1번째 (21장)
> 

### **🧐 : 플로우, 채널플로우, 콜백플로우 차이를 명확하게 알아야 사용할수 있을것 같은데… 아직까진 잘 와닿지 않네유..ㅜ**
