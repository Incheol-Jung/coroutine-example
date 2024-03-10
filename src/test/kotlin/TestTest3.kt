import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 *
 * @author Incheol.Jung
 * @since 2024. 03. 10.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SendNotificationsTest {
    data class Notification(val id: Int)

    interface NotificationsService {
        val notificationsSent: MutableSet<Notification>

        suspend fun send(notification: Notification)
    }

    class FakeNotificationsService(
        private val delayMillis: Long,
    ) : NotificationsService {
        override val notificationsSent: MutableSet<Notification> = mutableSetOf()



        override suspend fun send(notification: Notification) {
            notificationsSent.add(notification)
            delay(delayMillis)
        }
    }

    interface NotificationsRepository {
        val notificationsMarkedAsSent: MutableSet<Int>

        fun notificationsToSend(): List<Notification>

        suspend fun markAsSent(id: Int)
    }

    class FakeNotificationsRepository(
        private val delayMillis: Long,
        private val notifications: List<Notification>,
    ) : NotificationsRepository {
        override val notificationsMarkedAsSent: MutableSet<Int> = mutableSetOf()

        override fun notificationsToSend(): List<Notification> {
            return notifications
        }

        override suspend fun markAsSent(id: Int) {
            notificationsMarkedAsSent.add(id)
            delay(delayMillis)
        }
    }

    class NotificationsSender(
        private val notificationsRepository: NotificationsRepository,
        private val notificationsService: NotificationsService,
        private val notificationsScope: CoroutineScope,
    ) {
        fun sendNotifications() {
            notificationsScope.launch {
                val notifications =
                    notificationsRepository
                        .notificationsToSend()
                for (notification in notifications) {
                    launch {
                        notificationsService.send(notification)
                        notificationsRepository
                            .markAsSent(notification.id)
                    }
                }
            }
        }
    }

    companion object {

        @JvmField
        val name : String = ""
    }

    @Test
    fun testSendNotifications() {

        // given
        val notifications = List(100) { Notification(it) }
        val repo =
            FakeNotificationsRepository(
                delayMillis = 200,
                notifications = notifications,
            )
        val service =
            FakeNotificationsService(
                delayMillis = 300,
            )
        val testScope = TestScope()
        val sender =
            NotificationsSender(
                notificationsRepository = repo,
                notificationsService = service,
                notificationsScope = testScope,
            )

        // when
        sender.sendNotifications()
        testScope.advanceUntilIdle()

        // then all notifications are sent and marked
        assertEquals(
            notifications.toSet(),
            service.notificationsSent.toSet(),
        )
        assertEquals(
            notifications.map { it.id }.toSet(),
            repo.notificationsMarkedAsSent.toSet(),
        )

        // and notifications are sent concurrently
        assertEquals(500, testScope.currentTime) // FIXME expected should be 700, why?
    }
}


