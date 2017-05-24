package us.handstand.kartwheel.network

import okhttp3.mockwebserver.MockResponse
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.toJson
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API.claimTicket

class APITest {
    private val mockApi = MockAPI()
    private val lock = Object()
    private var response: Any? = null

    @Test
    fun claimTicket() {
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.team.toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))
        claimTicket(MockAPI.code1, object : API.APICallback<User> {
            override fun onSuccess(response: User) {
                this@APITest.response = response
                synchronized(lock, { lock.notifyAll() })
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                synchronized(lock, { lock.notifyAll() })
            }
        })
        synchronized(lock, { lock.wait() })

        assertThat(Storage.teamId, CoreMatchers.`is`(MockAPI.teamId))
        assertThat(Storage.eventId, CoreMatchers.`is`(MockAPI.eventId))
        assertThat(Storage.code, CoreMatchers.`is`(MockAPI.code1))
        assertThat(Storage.ticketId, CoreMatchers.`is`(MockAPI.ticketId1))
        assertTrue(response is User)
        assertThat((response as User).id(), CoreMatchers.`is`(Storage.userId))
        assertThat((response as User).teamId(), CoreMatchers.`is`(MockAPI.teamId))
    }

    @Test
    fun updateUser() {
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, false).toJson()))
        API.updateUser(User.emptyUser(), object : API.APICallback<User> {
            override fun onSuccess(response: User) {
                this@APITest.response = response
                synchronized(lock, { lock.notifyAll() })
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                synchronized(lock, { lock.notifyAll() })
            }
        })
        synchronized(lock, { lock.wait() })

        assertTrue(response is User)
        assertThat((response as User).birth(), CoreMatchers.`is`(MockAPI.birth1))
        assertThat((response as User).firstName(), CoreMatchers.`is`(MockAPI.firstName1))
    }

    @Test
    fun updateUserWithOnboarding() {
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, true).toJson()))
        API.updateUser(User.emptyUser(), object : API.APICallback<User> {
            override fun onSuccess(response: User) {
                this@APITest.response = response
                synchronized(lock, { lock.notifyAll() })
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                synchronized(lock, { lock.notifyAll() })
            }
        })
        synchronized(lock, { lock.wait() })

        assertTrue(response is User)
        assertThat((response as User).firstName(), CoreMatchers.`is`(MockAPI.firstName1))
        assertThat((response as User).buddyUrl(), CoreMatchers.`is`(MockAPI.buddyUrl1))
        assertThat((response as User).imageUrl(), CoreMatchers.`is`(MockAPI.imageUrl1))
        assertThat((response as User).birth(), CoreMatchers.`is`(MockAPI.birth1))
    }
}
