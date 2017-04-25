package us.handstand.kartwheel.controller

import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.RaceModel
import us.handstand.kartwheel.network.API


class RaceController(val db: BriteDatabase?, val eventId: String) {
    // Synchronize read/write to RaceController
    private val races = mutableListOf<Race>()
    private var listener: RaceControllerListener? = null

    companion object {
        interface RaceControllerListener {
            fun onRacesUpdated(races: List<Race>)
        }
    }

    init {
        // Subscribe to API response
        API.getRaces(eventId, object : API.APICallback<List<Race>> {
            override fun onSuccess(response: List<Race>) {
                synchronized(this@RaceController, {
                    races.clear()
                    races.addAll(response)
                    notifyListener()
                })
            }
        })

        // Subscribe to DB changes to the races.
        val selectAllRaces = Race.FACTORY.select_all()
        db?.createQuery(RaceModel.TABLE_NAME, selectAllRaces.statement, *selectAllRaces.args)
                ?.subscribe({
                    it.run()?.use {
                        synchronized(this@RaceController, {
                            races.clear()
                            while (it.moveToNext()) {
                                val race = Race.FACTORY.select_allMapper().map(it)
                                races.add(race)
                            }
                            notifyListener()
                        })
                    }
                })
    }

    private fun notifyListener() {
        synchronized(this@RaceController, {
            listener?.onRacesUpdated(arrayListOf(*races.toTypedArray()))
        })
    }


}