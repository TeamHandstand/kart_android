package us.handstand.kartwheel.controller


class RaceListController {
    var fragmentInterface: StartFragmentInterface? = null

    companion object {
        interface StartFragmentInterface {
            fun showRaceSignUp(raceId: String)
        }
    }

    fun onRaceItemClicked(raceId: String) {
        fragmentInterface?.showRaceSignUp(raceId)
    }
}
