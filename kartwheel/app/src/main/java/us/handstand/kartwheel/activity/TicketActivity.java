package us.handstand.kartwheel.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Response;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.fragment.AlreadyClaimedFragment;
import us.handstand.kartwheel.fragment.CodeEntryFragment;
import us.handstand.kartwheel.fragment.TOSFragment;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.Team;
import us.handstand.kartwheel.network.API;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {
    public interface TicketFragment {
        @Nullable
        Bundle onButtonClicked(@IdRes int id);
    }

    @IntDef({TOS, CODE_ENTRY, CRITICAL_INFO, ALREADY_CLAIMED, FORFEIT})
    private @interface FragmentType {
    }

    private static final String INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type";
    private static final int TOS = 0;
    private static final int CODE_ENTRY = 1;
    private static final int CRITICAL_INFO = 2;
    private static final int ALREADY_CLAIMED = 3;
    private static final int FORFEIT = 4;

    private TextView title;
    private AppCompatButton button;
    private AppCompatButton additionalButton;
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
        title = ViewUtil.findView(this, R.id.title_text);
        button = ViewUtil.findView(this, R.id.button);
        button.setOnClickListener(this);
        additionalButton = ViewUtil.findView(this, R.id.additional_button);
        additionalButton.setOnClickListener(this);
        showFragment(getCurrentFragmentType());
    }

    public void showFragment(@FragmentType int type) {
        title.setText(R.string.app_name);
        additionalButton.setVisibility(View.GONE);
        switch (type) {
            case TOS:
                ticketFragment = new TOSFragment();
                button.setText(R.string.scroll_down);
                break;
            case CODE_ENTRY:
                ticketFragment = new CodeEntryFragment();
                button.setText(R.string.lets_go);
                break;
            case ALREADY_CLAIMED:
                title.setText(R.string.already_claimed_title);
                button.setText(R.string.try_different_code);
                setAdditionalButtonState(R.color.green, R.string.contact_us, true);
                ticketFragment = new AlreadyClaimedFragment();
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

    public void setButtonState(@ColorRes int color, @StringRes int textRes, boolean enabled) {
        //noinspection unchecked
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(getResources().getColor(color)));
        button.setText(textRes);
        button.setEnabled(enabled);
    }

    public void setAdditionalButtonState(@ColorRes int color, @StringRes int textRes, boolean enabled) {
        //noinspection unchecked
        ViewCompat.setBackgroundTintList(additionalButton, ColorStateList.valueOf(getResources().getColor(color)));
        additionalButton.setText(textRes);
        additionalButton.setEnabled(enabled);
        if (enabled) {
            additionalButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (!v.isEnabled() || !(v instanceof AppCompatButton)) {
            return;
        }

        Bundle buttonClickedBundle = ticketFragment.onButtonClicked(v.getId());
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
                            switch (response.code()) {
                                case 200:
                                    // TODO
                                    break;
                                case 404:
                                    // TODO
                                    break;
                                case 409:
                                    runOnUiThread(() -> showFragment(ALREADY_CLAIMED));
                                    break;
                            }
                        }
                    });
                }
                break;
            case ALREADY_CLAIMED:
                if (v.getId() == R.id.additional_button) {
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.contact_us_subject_line));
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.contact_us_body));

                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.contact_us)));
                } else {
                    showFragment(CODE_ENTRY);
                }
                break;
        }
    }
}
