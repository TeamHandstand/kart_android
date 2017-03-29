package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.model.AndroidStorage;
import us.handstand.kartwheel.model.loader.GameInfoLoader;


public class GameInfoFragment extends Fragment implements TicketActivity.TicketFragment, LoaderManager.LoaderCallbacks {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_game_info, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new GameInfoLoader(getActivity(), AndroidStorage.getString(AndroidStorage.TEAM_ID));
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
