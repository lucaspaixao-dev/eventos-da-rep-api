package io.github.xuenqui.eventosdarep.domain.services

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceAlreadyExistsException
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import mocks.buildEventMock
import mocks.buildUserMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class EventServiceTest {

    private val eventRepository: EventRepository = mockk(relaxed = true)
    private val userService: UserService = mockk(relaxed = true)
    private val notificationService: NotificationService = mockk(relaxed = true)

    private val eventService = EventService(
        eventRepository,
        userService,
        notificationService
    )

    @Test
    fun `should create an event when there is not an anyone event with the same name`() {
        val eventMock = buildEventMock()
        val userMock = buildUserMock()
        val tokensMock = listOf(userMock.device!!.token)

        val title = "${eventMock.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        every { eventRepository.findByTitle(eventMock.title) } returns null
        every { eventRepository.create(eventMock) } returns eventMock.id!!
        every { userService.findAllWithoutPage() } returns listOf(userMock)
        every { notificationService.sendNotificationToTokens(title, message, tokensMock) } just Runs

        assertDoesNotThrow { eventService.create(eventMock) }

        verify(exactly = 1) { eventRepository.findByTitle(eventMock.title) }
        verify(exactly = 1) { eventRepository.create(eventMock) }
        verify(exactly = 1) { userService.findAllWithoutPage() }
        verify(exactly = 1) { notificationService.sendNotificationToTokens(title, message, tokensMock) }
    }

    @Test
    fun `should create an event when there is not an anyone event with the same name and the user doesnt have a device`() {
        val eventMock = buildEventMock()
        val userMock = buildUserMock().copy(device = null)
        val tokensMock = emptyList<String>()

        val title = "${eventMock.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        every { eventRepository.findByTitle(eventMock.title) } returns null
        every { eventRepository.create(eventMock) } returns eventMock.id!!
        every { userService.findAllWithoutPage() } returns listOf(userMock)
        every { notificationService.sendNotificationToTokens(title, message, tokensMock) } just Runs

        assertDoesNotThrow { eventService.create(eventMock) }

        verify(exactly = 1) { eventRepository.findByTitle(eventMock.title) }
        verify(exactly = 1) { eventRepository.create(eventMock) }
        verify(exactly = 1) { userService.findAllWithoutPage() }
        verify(exactly = 1) { notificationService.sendNotificationToTokens(title, message, tokensMock) }
    }

    @Test
    fun `should not create an event when there is an event with the same name`() {
        val eventMock = buildEventMock()

        every { eventRepository.findByTitle(eventMock.title) } returns eventMock

        assertThrows<ResourceAlreadyExistsException>("Event with title ${eventMock.title} already exists") {
            eventService.create(eventMock)
        }

        verify(exactly = 1) { eventRepository.findByTitle(eventMock.title) }
        verify(exactly = 0) { eventRepository.create(eventMock) }
        verify(exactly = 0) { userService.findAllWithoutPage() }
        verify(exactly = 0) { notificationService.sendNotificationToTokens(any(), any(), any()) }
    }

    @Test
    fun `should return the event by id`() {
        val eventMock = buildEventMock()
        val id = UUID.randomUUID().toString()

        every { eventRepository.findById(id) } returns eventMock

        val result = assertDoesNotThrow { eventService.findById(id) }

        assertThat(result).isNotNull()
        verify(exactly = 1) { eventRepository.findById(id) }
    }

    @Test
    fun `should throw an exception when the there is not an event with the same id`() {
        val id = UUID.randomUUID().toString()

        every { eventRepository.findById(id) } returns null

        assertThrows<ResourceNotFoundException>("Event not found") { eventService.findById(id) }

        verify(exactly = 1) { eventRepository.findById(id) }
    }

    @Test
    fun `should return all the events with a pagination`() {
        val eventMock = buildEventMock()
        val page = 0
        val size = 20

        every { eventRepository.findAll(page, size) } returns listOf(eventMock)

        val result = assertDoesNotThrow { eventService.findAll(page, size) }

        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        verify(exactly = 1) { eventRepository.findAll(page, size) }
    }

    @Test
    fun `should return all the events actives when there is an event bigger than now`() {
        val eventMock = buildEventMock().copy(end = LocalTime.now().plusMinutes(10))
        val page = 0
        val size = 20

        every { eventRepository.findByActive(true, page, size) } returns listOf(eventMock)

        val result = assertDoesNotThrow { eventService.findActiveEvents(page, size) }

        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        assertThat(result.first().id!!).isEqualTo(eventMock.id!!)

        verify(exactly = 1) { eventRepository.findByActive(true, page, size) }
    }

    @Test
    fun `should return all the events actives when there is an event smaller than now`() {
        val eventMock = buildEventMock().copy(end = LocalTime.now().minusMinutes(10))
        val page = 0
        val size = 20

        every { eventRepository.findByActive(true, page, size) } returns listOf(eventMock)

        val result = assertDoesNotThrow { eventService.findActiveEvents(page, size) }

        assertThat(result).isNotNull()
        assertThat(result).isEmpty()

        verify(exactly = 1) { eventRepository.findByActive(true, page, size) }
    }

    @Test
    fun `should update the event when there is an event on database`() {
        val existsEvent = buildEventMock().copy(users = listOf(mockk(relaxed = true)))
        val eventId = existsEvent.id!!

        val newEvent = buildEventMock().copy(
            title = "new title",
            latitude = -20.0,
            longitude = -30.0,
            city = "new city",
            address = "new address",
            description = "new description",
            photo = "new photo",
            date = LocalDateTime.now().plusDays(1),
            begin = LocalTime.now().plusHours(2),
            end = LocalTime.now().plusHours(3),
            active = false,
            users = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val expeted = newEvent.copy(
            id = existsEvent.id!!,
            users = existsEvent.users,
            createdAt = existsEvent.createdAt,
            updatedAt = LocalDateTime.now()
        )

        every { eventRepository.findById(eventId) } returns existsEvent
        every { eventRepository.update(eventId, any()) } returns expeted

        val result = assertDoesNotThrow { eventService.update(eventId, newEvent) }

        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(eventId)
        assertThat(result.title).isEqualTo(newEvent.title)
        assertThat(result.latitude).isEqualTo(newEvent.latitude)
        assertThat(result.longitude).isEqualTo(newEvent.longitude)
        assertThat(result.city).isEqualTo(newEvent.city)
        assertThat(result.address).isEqualTo(newEvent.address)
        assertThat(result.description).isEqualTo(newEvent.description)
        assertThat(result.photo).isEqualTo(newEvent.photo)
        assertThat(result.date).isEqualTo(newEvent.date)
        assertThat(result.begin).isEqualTo(newEvent.begin)
        assertThat(result.end).isEqualTo(newEvent.end)
        assertThat(result.active).isEqualTo(newEvent.active)
        assertThat(result.users).isEqualTo(existsEvent.users)
        assertThat(result.createdAt).isEqualTo(existsEvent.createdAt)
        assertThat(result.updatedAt).isNotNull()

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { eventRepository.update(eventId, any()) }
    }

    @Test
    fun `should join an event when the user is not going to the event`() {
        val existsUser = buildUserMock()
        val eventMock = buildEventMock().copy(users = listOf(existsUser))
        val eventId = eventMock.id!!

        val userMock = buildUserMock()
        val userId = userMock.id!!

        val title = "${userMock.name} confirmou presença! 🎉"
        val message = "${userMock.name} confirmou presença no evento ${eventMock.title}!"

        val tokensMock = listOf(userMock.device!!.token)

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock
        every { userService.findById(existsUser.id!!) } returns existsUser
        every { eventRepository.joinEvent(eventId, userId) } just Runs
        every { notificationService.sendNotificationToTokens(title, message, tokensMock) } just Runs

        assertDoesNotThrow { eventService.join(eventId, userId) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 1) { userService.findById(existsUser.id!!) }
        verify(exactly = 1) { eventRepository.joinEvent(eventId, userId) }
        verify(exactly = 1) { notificationService.sendNotificationToTokens(title, message, tokensMock) }
    }

    @Test
    fun `should not join an event when the user is going to the event`() {
        val userMock = buildUserMock()
        val userId = userMock.id!!

        val eventMock = buildEventMock().copy(users = listOf(userMock))
        val eventId = eventMock.id!!

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock

        assertDoesNotThrow { eventService.join(eventId, userId) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 0) { eventRepository.joinEvent(eventId, userId) }
        verify(exactly = 0) { notificationService.sendNotificationToTokens(any(), any(), any()) }
    }

    @Test
    fun `should throw an exception when the user does not have a device when try to join an event`() {
        val userMock = buildUserMock().copy(device = null)
        val userId = userMock.id!!

        val eventMock = buildEventMock().copy(users = emptyList())
        val eventId = eventMock.id!!

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock

        assertThrows<ValidationException>("User must have a device") { eventService.join(eventId, userId) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 0) { eventRepository.joinEvent(eventId, userId) }
        verify(exactly = 0) { notificationService.sendNotificationToTokens(any(), any(), any()) }
    }

    @Test
    fun `should exit to the event when the user is going to the event`() {
        val userMock = buildUserMock()
        val userId = userMock.id!!

        val eventMock = buildEventMock().copy(users = listOf(userMock))
        val eventId = eventMock.id!!

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock
        every { eventRepository.exitEvent(eventId, userId) } just Runs

        assertDoesNotThrow { eventService.remove(eventId, userId) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 1) { eventRepository.exitEvent(eventId, userId) }
    }

    @Test
    fun `should not exit to the event when the user is not going to the event`() {
        val userMock = buildUserMock()
        val userId = userMock.id!!

        val eventMock = buildEventMock().copy(users = emptyList())
        val eventId = eventMock.id!!

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock

        assertDoesNotThrow { eventService.remove(eventId, userId) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 0) { eventRepository.exitEvent(eventId, userId) }
    }

    @Test
    fun `should send notification to event when the users on event have the device`() {
        val userMock = buildUserMock()
        val userId = userMock.id!!
        val tokens = listOf(userMock.device!!.token)

        val eventMock = buildEventMock().copy(users = listOf(userMock))
        val eventId = eventMock.id!!

        val title = "test"
        val message = "message"
        val newTitle = "${eventMock.title}: $title"

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock
        every { notificationService.sendNotificationToTokens(newTitle, message, tokens) } just Runs

        assertDoesNotThrow { eventService.sendNotification(eventId, title, message) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 1) { notificationService.sendNotificationToTokens(newTitle, message, tokens) }
    }

    @Test
    fun `should not send notification to event when the users on event not have the device`() {
        val userMock = buildUserMock().copy(device = null)
        val userId = userMock.id!!
        val tokens = emptyList<String>()

        val eventMock = buildEventMock().copy(users = listOf(userMock))
        val eventId = eventMock.id!!

        val title = "test"
        val message = "message"
        val newTitle = "${eventMock.title}: $title"

        every { eventRepository.findById(eventId) } returns eventMock
        every { userService.findById(userId) } returns userMock
        every { notificationService.sendNotificationToTokens(newTitle, message, tokens) } just Runs

        assertDoesNotThrow { eventService.sendNotification(eventId, title, message) }

        verify(exactly = 1) { eventRepository.findById(eventId) }
        verify(exactly = 1) { userService.findById(userId) }
        verify(exactly = 1) { notificationService.sendNotificationToTokens(newTitle, message, tokens) }
    }
}
