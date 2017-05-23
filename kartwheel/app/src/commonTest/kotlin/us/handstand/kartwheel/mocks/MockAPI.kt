package us.handstand.kartwheel.mocks

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API

class MockAPI {
    val server = MockWebServer()
    val context = MockDBContext()

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        Storage.initialize(context.getSharedPreferences("", Context.MODE_PRIVATE))
        Database.initialize(context)
        Storage.clear()
        API.initialize(null, okHttpClient, server.url("/").uri().toString())
    }

    companion object {
        const val code = "matt"
        const val teamId = "80b1736f-88f3-4f2e-b8f7-f599b2ceca20"
        const val eventId = "73945c39-f0b7-47bc-a3e2-963532d02f94"
        const val ticketId = "212c5182-4ee4-402b-b7ae-784ecfb4b90e"
        const val teamResponse = "{\"team\":{\"id\":\"$teamId\",\"name\":\"team-1\",\"slug\":null,\"goldCount\":0,\"silverCount\":0,\"bronzeCount\":0,\"ribbonCount\":0,\"eventId\":\"$eventId\",\"users\":[{\"id\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"firstName\":\"Sam\",\"lastName\":\"Goldstein\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"sammy\",\"cell\":null,\"email\":\"sam@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/sam.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null},{\"id\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"firstName\":\"Josh\",\"lastName\":\"Zipin\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"nickName\":\"joshy\",\"cell\":null,\"email\":\"josh@handstandwith.us\",\"referralType\":null,\"imageUrl\":\"https://assets.handstandwith.us/web/team/zip.jpg\",\"pushEnabled\":false,\"facetimeCount\":0,\"pushDeviceToken\":null,\"pancakeOrWaffle\":null,\"charmanderOrSquirtle\":null}],\"tickets\":[{\"id\":\"$ticketId\",\"code\":\"$code\",\"claimedAt\":\"2017-04-17T08:48:40.778Z\",\"playerId\":\"0dc1a846-82e5-4fb0-bf37-da6d2ff5cb30\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null},{\"id\":\"18112c1a-6ab2-48d1-aeb5-92dfaeeffa89\",\"code\":\"testCode\",\"claimedAt\":null,\"playerId\":\"5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394\",\"teamId\":\"80b1736f-88f3-4f2e-b8f7-f599b2ceca20\",\"priceTierId\":null,\"paymentId\":null,\"forfeitedAt\":null,\"purchasedAt\":null}]}}"
        const val eventResponse = "{\"event\":{\"id\":\"$eventId\",\"endTime\":\"2017-05-19 03:07:43.537977\",\"startTime\":\"2017-05-17 03:07:43.537428\",\"name\":\"kartwheel\",\"createdAt\":\"2017-05-17 03:07:43.559896\",\"updatedAt\":\"2017-05-17 03:07:43.559896\",\"usersCanSeeRaces\":\"true\"}}"
        const val birth = "1989-07-25 00:00:00.000"
        const val userUpdateResponse = "{\"user\":{\"first_name\":\"adf\", \"last_name\":\"asdf\", \"nick_name\":\"matty\", \"cell\":\"4083064285\", \"email\":\"asdf\", \"birth\":\"$birth\", \"pancake_or_waffle\":\"waffle\", \"charmander_or_squirtle\":\"squirtle\", \"updated_at\":\"2017-04-21 01:32:40 UTC\", \"id\":\"934bd187-44a1-42b6-8412-74346e92470d\"}}"
    }
}