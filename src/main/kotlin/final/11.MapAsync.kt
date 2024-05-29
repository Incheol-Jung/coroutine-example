package final

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.random.Random
import kotlin.time.measureTimedValue

/**
 *
 * @author Incheol.Jung
 * @since 2024. 05. 29.
 */
suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }
        .awaitAll()
}

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

suspend fun main() {
    val times = measureTimedValue {
        val bestStudent = getBestStudent("test", StudentsRepository())
        println(bestStudent)
    }
    println(times)

    val times2 = measureTimedValue {
        val bestStudent = getBestStudent2("test", StudentsRepository())
        println(bestStudent)
    }
    println(times2)
}

suspend fun getBestStudent(
    semester: String,
    repo: StudentsRepository
): Student =
    repo.getStudentIds(semester)
        .mapAsync { repo.getStudent(it) }
        .maxBy { it.age }


suspend fun getBestStudent2(
    semester: String,
    repo: StudentsRepository
): Student =
    repo.getStudentIds(semester)
        .mapAsync(concurrencyLimit = 2) { repo.getStudent(it) }
        .maxBy { it.age }

class StudentsRepository {
    suspend fun getStudentIds(value: String): List<Number> {
        delay(2000)
        return listOf(
            Random.nextInt(),
            Random.nextInt(),
            Random.nextInt(),
            Random.nextInt(),
            Random.nextInt(),
            Random.nextInt()
        )
    }

    suspend fun getStudent(it: Number): Student {
        delay(2000)
        return Student(age = Random.nextInt(), name = Random.nextInt().toString())
    }
}

data class Student(
    val age: Int,
    val name: String,
)

