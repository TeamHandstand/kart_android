package us.handstand.kartwheel.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.fragment.AlreadyClaimedFragment;
import us.handstand.kartwheel.fragment.CodeEntryFragment;
import us.handstand.kartwheel.fragment.CriticalInformationFragment;
import us.handstand.kartwheel.fragment.TOSFragment;
import us.handstand.kartwheel.fragment.WelcomeFragment;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.AndroidStorage;
import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.Ticket;
import us.handstand.kartwheel.model.User;
import us.handstand.kartwheel.network.API;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {
    public interface TicketFragment extends View.OnClickListener {
    }

    @IntDef({TOS, CODE_ENTRY, CRITICAL_INFO, WELCOME, ALREADY_CLAIMED, FORFEIT})
    private @interface FragmentType {
    }

    private static final String INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type";
    private static final int TOS = 0;
    private static final int CODE_ENTRY = 1;
    private static final int CRITICAL_INFO = 2;
    private static final int WELCOME = 3;
    private static final int ALREADY_CLAIMED = 4;
    private static final int FORFEIT = 5;

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
            case WELCOME:
                title.setText(R.string.welcome);
                button.setText(R.string.im_ready);
                ticketFragment = new WelcomeFragment();
                break;
            case CRITICAL_INFO:
                title.setText(R.string.welcome);
                button.setText(R.string.next);
                ticketFragment = new CriticalInformationFragment();
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

        ticketFragment.onClick(v);
        switch (getCurrentFragmentType()) {
            case TOS:
                showFragment(CODE_ENTRY);
                break;
            case CODE_ENTRY:
                if (getIntent().hasExtra(Ticket.CODE)) {
                    API.claimTicket(getIntent().getStringExtra(Ticket.CODE), new API.APICallback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.code() == 200) {
                                runOnUiThread(() -> onSuccessfulClaim());
                            } else if (response.code() == 409) {
                                runOnUiThread(() -> showFragment(ALREADY_CLAIMED));
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject error = new JSONObject(response.errorBody().string());
                                    Toast.makeText(TicketActivity.this, error.getString("error"), Toast.LENGTH_LONG).show();
                                } catch (Exception ignore) {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            super.onFailure(call, t);
                            Toast.makeText(TicketActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
                            .putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.contact_us_body));
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
                    onSuccessfulClaim();
                }
                break;
        }
    }

    private void onSuccessfulClaim() {
        String ticketCode = getIntent().getStringExtra(Ticket.CODE);
        AndroidStorage.set(AndroidStorage.EMOJI_CODE, ticketCode);
        Database.get().createQuery(Ticket.TABLE_NAME, "select * from ticket where code is ?", ticketCode)
                .subscribe(query -> {
                    Cursor cursor = query.run();
                    if (cursor != null && cursor.moveToFirst()) {
                        Ticket ticket = Ticket.SELECT_ALL_MAPPER.map(cursor);
                        Log.e(TicketActivity.class.getName(), API.gson.toJson(ticket));
                        AndroidStorage.set(AndroidStorage.USER_ID, ticket.playerId());
                        runOnUiThread(() -> showFragment(CRITICAL_INFO));
                    }
                }, Throwable::printStackTrace);
    }
}
