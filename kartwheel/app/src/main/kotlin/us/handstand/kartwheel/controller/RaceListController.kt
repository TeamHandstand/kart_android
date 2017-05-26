package us.handstand.kartwheel.controller

import rx.Subscription
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.RaceModel
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API


open class RaceListController {
    private var raceListListener: RaceListListener? = null
    private var subscription: Subscription? = null

    interface RaceListListener {
        fun onRacesUpdated(races: List<Race>)
        fun onRaceItemClicked(raceId: String)
    }

    open fun subscribe(raceListListener: RaceListListener) {
        this.raceListListener = raceListListener
        API.getRacesWithCourses(Storage.eventId)
        val raceQuery = Race.FACTORY.select_for_event_id(Storage.eventId)
        subscription = Database.get().createQuery(RaceModel.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToList { Race.FACTORY.select_for_event_idMapper().map(it) }
                .subscribe { onRacesUpdated(it) }
    }

    open fun unsubscribe() {
        subscription?.unsubscribe()
        raceListListener = null
    }

    open fun onRacesUpdated(races: List<Race>) {
        raceListListener?.onRacesUpdated(races)
    }

    fun onRaceItemClicked(raceId: String) {
        raceListListener?.onRaceItemClicked(raceId)
    }
}
