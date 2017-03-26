package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.layout.ViewUtil;

public class TOSFragment extends Fragment implements TicketActivity.TicketFragment, ViewTreeObserver.OnScrollChangedListener {

    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup fragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_tos, container, false);
        scrollView = ViewUtil.findView(fragmentView, R.id.tos_scroll_view);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TicketActivity) getActivity()).setButtonState(R.color.grey_button_disabled, R.string.scroll_down, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);
    }

    @Override
    public void onScrollChanged() {
        int yPos = scrollView.getScrollY();
        if (yPos >= scrollView.getMaxScrollAmount()) {
            onScrollComplete();
        }
    }

    private void onScrollComplete() {
        ((TicketActivity) getActivity()).setButtonState(R.color.blue, R.string.lets_go, true);
    }

    @Override
    public void onClick(View v) {
    }
}
