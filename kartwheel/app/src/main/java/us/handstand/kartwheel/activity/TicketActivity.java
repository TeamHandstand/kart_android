package us.handstand.kartwheel.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.fragment.AlreadyClaimedFragment;
import us.handstand.kartwheel.fragment.CodeEntryFragment;
import us.handstand.kartwheel.fragment.CriticalInformationFragment;
import us.handstand.kartwheel.fragment.TOSFragment;
import us.handstand.kartwheel.fragment.WelcomeFragment;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.AndroidStorage;
import us.handstand.kartwheel.model.Race;
import us.handstand.kartwheel.model.Ticket;
import us.handstand.kartwheel.model.User;
import us.handstand.kartwheel.network.API;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {
    public interface TicketFragment extends View.OnClickListener {
    }

    @IntDef({TOS, CODE_ENTRY, CRITICAL_INFO, WELCOME, ALREADY_CLAIMED, FORFEIT, GAME_INFO})
    private @interface FragmentType {
    }

    private static final String INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type";
    private static final int TOS = 0;
    private static final int CODE_ENTRY = 1;
    private static final int CRITICAL_INFO = 2;
    private static final int WELCOME = 3;
    private static final int ALREADY_CLAIMED = 4;
    private static final int FORFEIT = 5;
    private static final int GAME_INFO = 6;

    @BindView(R.id.title_text) TextView title;
    @BindView(R.id.button) AppCompatButton button;
    @BindView(R.id.additional_button) AppCompatButton additionalButton;
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
        ButterKnife.bind(this);
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
            case CRITICAL_INFO:
                title.setText(R.string.welcome);
                button.setText(R.string.next);
                ticketFragment = new CriticalInformationFragment();
                break;
            case WELCOME:
                title.setText(R.string.welcome);
                ticketFragment = new WelcomeFragment();
                setButtonState(R.color.grey_button_disabled, R.string.im_ready, false);
                break;
        }
        if (ticketFragment != null) {
            getIntent().putExtra(INTENT_EXTRA_FRAGMENT_TYPE, type);
            if (!isFinishing() && !getSupportFragmentManager().isDestroyed()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, (Fragment) ticketFragment).commit();
            }
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

        ticketFragment.onClick(v);
        switch (getCurrentFragmentType()) {
            case TOS:
                showFragment(CODE_ENTRY);
                break;
            case CODE_ENTRY:
                if (getIntent().hasExtra(Ticket.CODE)) {
                    API.claimTicket(getIntent().getStringExtra(Ticket.CODE), new API.APICallback<Ticket>() {
                        @Override
                        public void onSuccess(Ticket ticket) {
                            if (ticket.isClaimed()) {
                                API.getRaces(AndroidStorage.getString(AndroidStorage.EVENT_ID), new API.APICallback<List<Race>>() {
                                    @Override
                                    public void onSuccess(List<Race> response) {
                                        showFragment(GAME_INFO);
                                    }

                                    @Override
                                    public void onFailure(int errorCode, String errorResponse) {
                                        Toast.makeText(TicketActivity.this, "Failed to get races " + errorResponse, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                runOnUiThread(() -> showFragment(CRITICAL_INFO));
                            }
                        }

                        @Override
                        public void onFailure(int errorCode, String errorResponse) {
                            if (errorCode == 409) {
                                runOnUiThread(() -> showFragment(ALREADY_CLAIMED));
                            } else if (errorResponse != null) {
                                Toast.makeText(TicketActivity.this, errorResponse, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case ALREADY_CLAIMED:
                if (v.getId() == R.id.additional_button) {
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND)
                            .setType("plain/text")
                            .putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.support_email)})
                            .putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.contact_us_subject_line))
                            .putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.contact_us_body, getIntent().getStringExtra(Ticket.CODE)));
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.contact_us)));
                } else {
                    showFragment(CODE_ENTRY);
                }
                break;
            case CRITICAL_INFO:
                if (getIntent().hasExtra(User.PANCAKEORWAFFLE) &&
                        getIntent().hasExtra(User.CHARMANDERORSQUIRTLE)) {
                    showFragment(WELCOME);
                }
                break;
            case WELCOME:
                if (getIntent().hasExtra(User.BIRTH) &&
                        getIntent().hasExtra(User.CELL) &&
                        getIntent().hasExtra(User.EMAIL) &&
                        getIntent().hasExtra(User.FIRSTNAME) &&
                        getIntent().hasExtra(User.LASTNAME) &&
                        getIntent().hasExtra(User.NICKNAME)) {
                    User user = User.FACTORY.creator.create(AndroidStorage.getString(AndroidStorage.USER_ID),
                            null,  // authToken
                            getIntent().getStringExtra(User.BIRTH),
                            getIntent().getStringExtra(User.CELL),
                            getIntent().getStringExtra(User.CHARMANDERORSQUIRTLE),
                            getIntent().getStringExtra(User.EMAIL),
                            AndroidStorage.getString(AndroidStorage.EVENT_ID),
                            null, // facetime count
                            getIntent().getStringExtra(User.FIRSTNAME),
                            null, // imageUrl
                            getIntent().getStringExtra(User.LASTNAME),
                            null, // miniGameId
                            getIntent().getStringExtra(User.NICKNAME),
                            getIntent().getStringExtra(User.PANCAKEORWAFFLE),
                            null, // device token
                            null, // push enabled
                            null, // race id
                            null, // referral type
                            null, // team id
                            null, // total anti miles
                            null, // total distance miles
                            null // updated at
                    );
                    API.updateUser(user, new API.APICallback<User>() {
                        @Override
                        public void onSuccess(User response) {
                            runOnUiThread(() -> showFragment(GAME_INFO));
                        }

                        @Override
                        public void onFailure(int errorCode, String errorResponse) {
                            Toast.makeText(TicketActivity.this, "Unable to create user: " + errorResponse, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
}
