package us.handstand.kartwheel.layout

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import us.handstand.kartwheel.R
import us.handstand.kartwheel.fragment.LogoutFragment
import us.handstand.kartwheel.fragment.MiniGameTypeFragment
import us.handstand.kartwheel.fragment.race.RaceListFragment


class LoggedInPagerAdapter(val context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = getFragmentFromType(position)

    override fun getCount(): Int = 3

    companion object {
        const val RACE_LIST = 0
        const val LEADERBOARD = 1
        const val ME = 2
        const val GAMES = 3

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
                LEADERBOARD -> return "Leader Board"
                ME -> return "Logout"
            }
            return "Placeholder"
        }

        fun getIconFromType(type: Int): Int {
            when (type) {
                RACE_LIST -> return R.drawable.tab_bar_races
                GAMES -> return R.drawable.tab_bar_mini_games
                LEADERBOARD -> return R.drawable.tab_bar_leaderboard
                ME -> return R.drawable.tab_bar_shoe
            }
            return R.drawable.tab_bar_races
        }

    }
}