package us.handstand.kartwheel.layout

import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import us.handstand.kartwheel.fragment.LogoutFragment
import us.handstand.kartwheel.fragment.MiniGameTypeFragment
import us.handstand.kartwheel.fragment.race.RaceListFragment


class LoggedInPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return getFragmentFromType(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return getTitleFromType(position)
    }

    override fun getCount(): Int {
        return 4
    }

    companion object {
        const val RACE_LIST = 0
        const val GAMES = 1
        const val LEADERBOARD = 2
        const val ME = 3

        @IntDef(RACE_LIST.toLong(), GAMES.toLong(), LEADERBOARD.toLong(), ME.toLong())
        annotation class LoggedInFragment

        fun getFragmentFromType(type: Int): Fragment {
            when (type) {
                RACE_LIST -> return RaceListFragment()
                GAMES -> return MiniGameTypeFragment()
                ME -> return LogoutFragment()
            }
            return Fragment()
        }

        fun getTitleFromType(type: Int): String {
            when (type) {
                RACE_LIST -> return "Races"
                GAMES -> return "Games"
                LEADERBOARD -> return "Leader board"
                ME -> return "Logout"
            }
            return "Placeholder"
        }

    }
}