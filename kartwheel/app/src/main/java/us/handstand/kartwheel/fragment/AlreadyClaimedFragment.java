package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;

public class AlreadyClaimedFragment extends Fragment implements TicketActivity.TicketFragment, View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup fragmentViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_already_claimed, container, false);
        fragmentViewGroup.findViewById(R.id.already_claimed_link).setOnClickListener(this);
        return fragmentViewGroup;
    }

    @Nullable
    @Override
    public Bundle onButtonClicked(@IdRes int id) {
        return null;
    }

    @Override
    public void onClick(View v) {
        // TODO: Where do we send them?
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
    }
}
