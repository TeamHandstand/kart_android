package us.handstand.kartwheel.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rx.Subscription
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.MiniGameTypeAdapter
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.MiniGameType
import us.handstand.kartwheel.model.MiniGameTypeModel
import us.handstand.kartwheel.network.API

class MiniGameTypeFragment : Fragment() {
    var subscription: Subscription? = null
    lateinit var adapter: MiniGameTypeAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_mini_game_type, container, false) as ViewGroup
        val recyclerView = ViewUtil.findView<RecyclerView>(fragmentView, R.id.miniGameTypeRecyclerView)
        adapter = MiniGameTypeAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerView.adapter = adapter
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        API.getMiniGameTypes()
        val query = MiniGameType.FACTORY.select_all()
        subscription = Database.get().createQuery(MiniGameTypeModel.TABLE_NAME, query.statement, *query.args)
                .mapToList { MiniGameType.FACTORY.select_allMapper().map(it) }
                .subscribe { adapter.setMiniGameTypes(it) }
    }

    override fun onPause() {
        subscription?.unsubscribe()
        super.onPause()
    }
}

