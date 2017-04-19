package us.handstand.kartwheel.network

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.mocks.MockDBContext
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.network.API.claimTicket

class APITest {
    private val okHttpClient = OkHttpClient.Builder().build()
    private val server = MockWebServer()
    private val teamResponse = "{\"team\":{\"id\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"name\":\"team-1\",\"slug\":null,\"goldCount\":0,\"silverCount\":0,\"bronzeCount\":0,\"ribbonCount\":0,\"eventId\":\"73945c39-f0b7-47bc-a3e2-963532d02f94\",\"users\":[{\"id\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"firstName\":\"Sam\",\"lastName\":\"Goldstein\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"sammy\",\"cell\":null,\"email\":\"sam@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/sam.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null},{\"id\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"firstName\":\"Josh\",\"lastName\":\"Zipin\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"joshy\",\"cell\":null,\"email\":\"josh@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/zip.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null}],\"tickets\":[{\"id\":\"212c5182-4ee4-402b-b7ae-784ecfb4b90e\",\"code\":\"matt\",\"claimedAt\":\"2017-04-17T08:48:40.778Z\",\"playerId\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null},{\"id\":\"18112c1a-6ab2-48d1-aeb5-92dfaeeffa89\",\"code\":\"testCode\",\"claimedAt\":null,\"playerId\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null}]}}"
    private val context = MockDBContext()

    @Before
    fun setUp() {
        Storage.initialize(context)
        Database.initialize(context)
        API.initialize(null, okHttpClient, BuildConfig.SERVER)
    }

    @Test
    fun claimTicket() {
        server.enqueue(MockResponse().setBody(teamResponse))
        server.start()

        val url = server.url("tickets/claim")
        claimTicket("testCode", object : API.APICallback<Ticket>() {
            override fun onSuccess(response: Ticket) {
                assertThat(response.code(), CoreMatchers.`is`("matt"))
                System.out.println("HI")
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                System.out.println("FUCK")
                fail()
            }
        })
    }
}
