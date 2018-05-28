package us.handstand.kartwheel.interfaces

import us.handstand.kartwheel.location.UserLocation

interface RaceFragmentInterface {
    fun getLocation(): UserLocation
}