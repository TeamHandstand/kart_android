package us.handstand.kartwheel.controller

import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.disposables.Disposable
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.notifications.PubNubManager

data class RegistrantInfo(val firstName: String? = "", val imageUrl: String? = "")

interface RaceSignUpControllerListener {
    fun onRegistrantsUpdated(registrantInfos: List<RegistrantInfo>)
    fun onRaceUpdated(race: Race.RaceWithCourse)
    fun onTopThreeUpdated(topThree: List<User>)
}

class RaceSignUpController(val db: BriteDatabase?, val eventId: String, val raceId: String, val listener: RaceSignUpControllerListener) {
    private var raceDisposable: Disposable? = null
    private var registrantDisposable: Disposable? = null
    private var topThreeDisposable: Disposable? = null
    var race: Race.RaceWithCourse? = null
    var userInRace = false

    fun subscribe() {
        // Listen for Race updates
        val raceQuery = Race.FACTORY.select_race_with_course(raceId)
        raceDisposable = Database.get().createQuery(RaceModel.RACEWITHCOURSE_VIEW_NAME, raceQuery.statement, *raceQuery.args)
                .mapToOne { Race.RACE_WITH_COURSE_SELECT.map(it) }
                .subscribe {
                    this.race = it
                    listener.onRaceUpdated(it)
                }
        // Listen for registrant updates
        val registrantQuery = User.FACTORY.select_for_race_id(raceId)
        registrantDisposable = db?.createQuery(UserModel.TABLE_NAME, registrantQuery.statement, *registrantQuery.args)
                ?.mapToList { User.FACTORY.select_for_race_idMapper().map(it) }
                ?.subscribe { onRegistrantsUpdated(it) }
        // Listen for the top three registrants for this race
//        val topThreeQuery = User.FACTORY.select_top_three_for_race(raceId)
//        topThreeDisposable = db?.createQuery(UserModel.TABLE_NAME, topThreeQuery.statement, *topThreeQuery.args)
//                ?.mapToList { User.FACTORY.select_top_three_for_raceMapper().map(it) }
//                ?.subscribe { listener.onTopThreeUpdated(it) }
        API.getUserRaceInfos(eventId, raceId)
//        API.getTopCourseTimes(eventId, raceId)
        PubNubManager.subscribe(PubNubManager.PubNubChannelType.raceRoomChannel, raceId)
    }


    fun dispose() {
        raceDisposable?.dispose()
        registrantDisposable?.dispose()
        topThreeDisposable?.dispose()
        PubNubManager.unsubscribe(PubNubManager.PubNubChannelType.raceRoomChannel, raceId)
    }

    private fun onRegistrantsUpdated(users: List<User>) {
        val registrantInfos = mutableListOf<RegistrantInfo>()
        val userId = Storage.userId
        userInRace = false
        for (user in users) {
            if (user.imageUrl() != null) {
                registrantInfos.add(RegistrantInfo(user.firstName(), user.imageUrl()))
            }
            if (user.id() == userId) {
                userInRace = true
            }
        }
        listener.onRegistrantsUpdated(registrantInfos)
    }
}