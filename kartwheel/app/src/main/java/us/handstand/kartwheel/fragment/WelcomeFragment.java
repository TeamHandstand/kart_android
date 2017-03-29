package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.User;
import us.handstand.kartwheel.util.DateFormatter;

public class WelcomeFragment extends Fragment implements TicketActivity.TicketFragment, TextWatcher {

    @BindView(R.id.birth)
    EditText birth;
    @BindView(R.id.cell)
    EditText cell;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.nickname)
    EditText nickname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, fragmentView);
        birth.addTextChangedListener(this);
        cell.addTextChangedListener(this);
        email.addTextChangedListener(this);
        firstName.addTextChangedListener(this);
        lastName.addTextChangedListener(this);
        nickname.addTextChangedListener(this);
        return fragmentView;
    }

    private boolean isValidInput() {
        // TODO: better validation, birth and cell not required
        return !ViewUtil.isEmpty(birth) && !ViewUtil.isEmpty(cell) && !ViewUtil.isEmpty(email) &&
                !ViewUtil.isEmpty(firstName) && !ViewUtil.isEmpty(lastName) && !ViewUtil.isEmpty(nickname);
    }

    @Override
    public void onClick(View v) {
        if (isValidInput()) {
            getActivity().getIntent().putExtra(User.BIRTH, DateFormatter.getString(DateFormatter.get(birth.getText().toString())));
            getActivity().getIntent().putExtra(User.CELL, cell.getText().toString());
            getActivity().getIntent().putExtra(User.EMAIL, email.getText().toString());
            getActivity().getIntent().putExtra(User.FIRSTNAME, firstName.getText().toString());
            getActivity().getIntent().putExtra(User.LASTNAME, lastName.getText().toString());
            getActivity().getIntent().putExtra(User.NICKNAME, nickname.getText().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isValidInput = isValidInput();
        ((TicketActivity) getActivity()).setButtonState(isValidInput ? R.color.blue : R.color.grey_button_disabled, R.string.im_ready, isValidInput);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
