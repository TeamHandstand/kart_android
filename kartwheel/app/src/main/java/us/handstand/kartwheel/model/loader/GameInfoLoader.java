package us.handstand.kartwheel.model.loader;

import android.content.Context;
import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.Collections;
import java.util.List;

import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.Team;

public class GameInfoLoader extends CachedAsyncLoader<List<String>> {

    private static final String TAG = GameInfoLoader.class.getName();
    private final String teamId;

    public GameInfoLoader(Context context, String teamId) {
        super(context);
        this.teamId = teamId;
    }

    @Override
    public List<String> loadInBackground() {
        BriteDatabase db = Database.get();
        SqlDelightStatement statement = Team.FACTORY.select_all(teamId);
        Team team = null;
        try (Cursor cursor = db.query(statement.statement, statement.args)) {
            if (cursor.moveToFirst()) {
                team = Team.SELECT_ALL_MAPPER.map(cursor);
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
        return Collections.emptyList();
    }
}
