package us.handstand.kartwheel.model.loader

import android.content.Context
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Team

class GameInfoLoader(context: Context, private val teamId: String) : CachedAsyncLoader<List<String>>(context) {

    override fun loadInBackground(): List<String> {
        val db = Database.get()
        val statement = Team.FACTORY.select_all(teamId)
        var team: Team? = null
        db.query(statement.statement, *statement.args).use { cursor ->
            if (cursor.moveToFirst()) {
                team = Team.SELECT_ALL_MAPPER.map(cursor)
            }
        }

        // TODO:construct adapter and views for users and team name
        /*List<ProtocolApi> result = new ArrayList<>();
        result.add(ProtocolApi.FACTORY.creator.create(-2, "", "", "Required", protocol, true, "string", "any"));
        result.add(ProtocolApi.FACTORY.creator.create(-2, "", "", "Optional", protocol, false, "string", "any"));
        result.add(ProtocolApi.FACTORY.creator.create(-1, "", "Give a name to this action", "Name", protocol, true, "string", "any"));



        List<ProtocolUi> ui = new ArrayList<>();
        SqlDelightStatement uiStatement = ProtocolUi.FACTORY.select_all(protocol);
        try (Cursor cursor = db.query(uiStatement.statement, uiStatement.args)) {
            if (cursor.moveToFirst()) {
                do {
                    ui.add(ProtocolUi.SELECT_ALL_MAPPER.map(cursor));
                } while (cursor.moveToNext());
            }
        }
        result.forEach(protocolApi -> {
            protocolApi.ui.addAll(ui);
        });

        Collections.sort(result);*/
        return emptyList()
    }

    companion object {

        private val TAG = GameInfoLoader::class.java.name
    }
}
