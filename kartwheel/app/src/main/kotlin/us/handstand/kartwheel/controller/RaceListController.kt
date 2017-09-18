package us.handstand.kartwheel.controller

import io.reactivex.disposables.Disposable
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.RaceModel
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API


open class RaceListController {
    private var raceListListener: RaceListListener? = null
    private var disposable: Disposable? = null

    interface RaceListListener {
        fun onRacesUpdated(races: List<Race.RaceWithCourse>)
        fun onRaceItemClicked(raceId: String)
    }

    open fun subscribe(raceListListener: RaceListListener) {
        this.raceListListener = raceListListener
        API.getRacesWithCourses(Storage.eventId)
        val raceQuery = Race.FACTORY.select_races_with_course_from_event(Storage.eventId)
        disposable = Database.get().createQuery(RaceModel.RACEWITHCOURSE_VIEW_NAME, raceQuery.statement, *raceQuery.args)
                .mapToList { Race.RACES_WITH_COURSE_FROM_EVENT_SELECT.map(it) }
                .subscribe { onRacesUpdated(it) }
    }

    open fun dispose() {
        disposable?.dispose()
        raceListListener = null
    }

    open fun onRacesUpdated(races: List<Race.RaceWithCourse>) {
        raceListListener?.onRacesUpdated(races)
    }

    fun onRaceItemClicked(raceId: String) {
        raceListListener?.onRaceItemClicked(raceId)
    }
}
