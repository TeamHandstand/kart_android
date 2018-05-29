package us.handstand.kartwheel.controller

import io.reactivex.disposables.Disposable
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.RaceModel
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.mapToList


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
        val raceQuery = Race.FACTORY.select_all();
        disposable = Database.get().createQuery(RaceModel.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToList { Race.FACTORY.select_allMapper().map(it) }
                .subscribe {
                    findAllRacesWithCourse(it)
                }
    }

    open fun dispose() {
        disposable?.dispose()
        raceListListener = null
    }

    private fun findAllRacesWithCourse(races: List<Race>) {
        val statement = Race.FACTORY.select_races_with_course_from_event(Storage.eventId)
        val items = Database.get().query(statement.statement, *statement.args).mapToList(Race.RACES_WITH_COURSE_FROM_EVENT_SELECT)
        onRacesUpdated(items)
    }

    open fun onRacesUpdated(races: List<Race.RaceWithCourse>) {
        raceListListener?.onRacesUpdated(races)
    }

    fun onRaceItemClicked(raceId: String) {
        raceListListener?.onRaceItemClicked(raceId)
    }
}
