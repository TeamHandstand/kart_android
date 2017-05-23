package us.handstand.kartwheel.network

import okhttp3.mockwebserver.MockResponse
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API.claimTicket
import us.handstand.kartwheel.util.DateFormatter

class APITest {
    private val mockApi = MockAPI()
    private val lock = Object()
    private var response: Any? = null

    @Test
    fun claimTicket() {
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.teamResponse))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.eventResponse))
        claimTicket("matt", object : API.APICallback<User> {
            override fun onSuccess(response: User) {
                this@APITest.response = response
                synchronized(lock, { lock.notifyAll() })
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                synchronized(lock, { lock.notifyAll() })
            }
        })
        synchronized(lock, { lock.wait() })

        assertThat(Storage.code, CoreMatchers.`is`(MockAPI.code))
        assertThat(Storage.teamId, CoreMatchers.`is`(MockAPI.teamId))
        assertThat(Storage.eventId, CoreMatchers.`is`(MockAPI.eventId))
        assertThat(Storage.ticketId, CoreMatchers.`is`(MockAPI.ticketId))
        assertTrue(response is User)
        assertThat((response as User).id(), CoreMatchers.`is`(Storage.userId))
        assertThat((response as User).teamId(), CoreMatchers.`is`(MockAPI.teamId))
    }

    @Test
    fun updateUser() {
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.userUpdateResponse))
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
        assertThat(DateFormatter.getString((response as User).birth()!!), CoreMatchers.`is`(MockAPI.birth))
    }
}
