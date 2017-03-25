package us.handstand.kartwheel.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import retrofit2.Call;
import retrofit2.Response;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.fragment.CodeEntryFragment;
import us.handstand.kartwheel.fragment.TOSFragment;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.Team;
import us.handstand.kartwheel.network.API;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {
    public interface TicketFragment {
        @Nullable
        Bundle onButtonClicked(AppCompatButton button);

        @TicketActivity.FragmentType
        int getFragmentType();
    }

    @IntDef({TOS, CODE_ENTRY, CRITICAL_INFO, INVALID, FORFEIT})
    public @interface FragmentType {
    }

    public static final String INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type";
    public static final int TOS = 0;
    public static final int CODE_ENTRY = 1;
    public static final int CRITICAL_INFO = 2;
    public static final int INVALID = 3;
    public static final int FORFEIT = 4;

    private AppCompatButton button;
    private TicketFragment ticketFragment;

    static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, TicketActivity.class);
        intent.putExtra(INTENT_EXTRA_FRAGMENT_TYPE, TOS);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        button = ViewUtil.findView(this, R.id.button);
        button.setOnClickListener(this);
        showFragment(getCurrentFragmentType());
    }

    public void showFragment(@FragmentType int type) {
        switch (type) {
            case TOS:
                ticketFragment = new TOSFragment();
                break;
            case CODE_ENTRY:
                ticketFragment = new CodeEntryFragment();
                break;
        }
        if (ticketFragment != null) {
            getIntent().putExtra(INTENT_EXTRA_FRAGMENT_TYPE, type);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, (Fragment) ticketFragment).commit();
        }
    }

    @FragmentType
    private int getCurrentFragmentType() {
        @FragmentType int currentFragmentType = getIntent().getIntExtra(INTENT_EXTRA_FRAGMENT_TYPE, TOS);
        return currentFragmentType;
    }

    public void setButtonState(int color, @StringRes int textRes, boolean enabled) {
        //noinspection unchecked
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(color));
        button.setText(textRes);
        button.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        if (!v.isEnabled() || !(v instanceof AppCompatButton)) {
            return;
        }

        Bundle buttonClickedBundle = ticketFragment.onButtonClicked((AppCompatButton) v);
        switch (getCurrentFragmentType()) {
            case TOS:
                showFragment(CODE_ENTRY);
                break;
            case CODE_ENTRY:
                if (buttonClickedBundle != null && buttonClickedBundle.containsKey(CodeEntryFragment.BUNDLE_CODE_KEY)) {
                    String ticketCode = buttonClickedBundle.getString(CodeEntryFragment.BUNDLE_CODE_KEY, "");
                    API.claimTicket(ticketCode, new API.APICallback<Team>() {
                        @Override
                        public void onResponse(Call<Team> call, Response<Team> response) {
                            response.body().id();
                            Log.e(TicketActivity.class.getName(), response.message());
                        }
                    });
                }
                break;
        }
    }
}
