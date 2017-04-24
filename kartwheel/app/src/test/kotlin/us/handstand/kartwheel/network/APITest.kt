package us.handstand.kartwheel.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import us.handstand.kartwheel.mocks.MockDBContext
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API.claimTicket
import us.handstand.kartwheel.util.DateFormatter
import java.util.*

class APITest {
    private val okHttpClient = OkHttpClient.Builder().build()
    private val server = MockWebServer()
    private val code = "matt"
    private val teamId = "80b1736f-88f3-4f2e-b8f7-f599b2ceca20"
    private val eventId = "73945c39-f0b7-47bc-a3e2-963532d02f94"
    private val teamResponse = "{\"team\":{\"id\":\"$teamId\",\"name\":\"team-1\",\"slug\":null,\"goldCount\":0,\"silverCount\":0,\"bronzeCount\":0,\"ribbonCount\":0,\"eventId\":\"$eventId\",\"users\":[{\"id\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"firstName\":\"Sam\",\"lastName\":\"Goldstein\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"sammy\",\"cell\":null,\"email\":\"sam@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/sam.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null},{\"id\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"firstName\":\"Josh\",\"lastName\":\"Zipin\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"joshy\",\"cell\":null,\"email\":\"josh@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/zip.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null}],\"tickets\":[{\"id\":\"212c5182-4ee4-402b-b7ae-784ecfb4b90e\",\"code\":\"$code\",\"claimedAt\":\"2017-04-17T08:48:40.778Z\",\"playerId\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null},{\"id\":\"18112c1a-6ab2-48d1-aeb5-92dfaeeffa89\",\"code\":\"testCode\",\"claimedAt\":null,\"playerId\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null}]}}"
    private val birth = "1989-07-25 00:00:00 UTC"
    private val userUpdateResponse = "{\"user\":{\"first_name\":\"adf\", \"last_name\":\"asdf\", \"nick_name\":\"matty\", \"cell\":\"4083064285\", \"email\":\"asdf\", \"birth\":\"$birth\", \"pancake_or_waffle\":\"waffle\", \"charmander_or_squirtle\":\"squirtle\", \"updated_at\":\"2017-04-21 01:32:40 UTC\", \"id\":\"934bd187-44a1-42b6-8412-74346e92470d\"}}"
    private val context = MockDBContext()
    private val lock = Object()
    private var response: Any? = null

    @Before
    fun setUp() {
        Storage.initialize(context.getSharedPreferences("preferences", Context.MODE_PRIVATE))
        Database.initialize(context)
        Storage.clear()
        API.initialize(null, okHttpClient, server.url("/").uri().toString())
    }

    @Test
    fun claimTicket() {
        server.enqueue(MockResponse().setBody(teamResponse))
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

        assertThat(Storage.code, CoreMatchers.`is`(code))
        assertThat(Storage.teamId, CoreMatchers.`is`(teamId))
        assertThat(Storage.eventId, CoreMatchers.`is`(eventId))
        assertTrue(response is User)
        assertThat((response as User).id(), CoreMatchers.`is`(Storage.userId))
        assertThat((response as User).teamId(), CoreMatchers.`is`(teamId))
    }

    @Test
    fun updateUser() {
        server.enqueue(MockResponse().setBody(userUpdateResponse))
        API.updateUser(User.FACTORY.creator.create("", "", Date(), "", "", "", "", 0L, "", "", "", "", "", "", "", false, "", "", "", 0.0, 0.0, ""),
                object : API.APICallback<User> {
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
        assertThat(DateFormatter.getString((response as User).birth()!!), CoreMatchers.`is`(birth))
    }
}
