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

import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.User;

public class WelcomeFragment extends Fragment implements TicketActivity.TicketFragment, TextWatcher {

    private EditText birth;
    private EditText cell;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText nickname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup fragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome, container, false);
        birth = ViewUtil.findView(fragmentView, R.id.birth);
        birth.addTextChangedListener(this);
        cell = ViewUtil.findView(fragmentView, R.id.cell);
        cell.addTextChangedListener(this);
        email = ViewUtil.findView(fragmentView, R.id.email);
        email.addTextChangedListener(this);
        firstName = ViewUtil.findView(fragmentView, R.id.first_name);
        firstName.addTextChangedListener(this);
        lastName = ViewUtil.findView(fragmentView, R.id.last_name);
        lastName.addTextChangedListener(this);
        nickname = ViewUtil.findView(fragmentView, R.id.nickname);
        nickname.addTextChangedListener(this);
        return fragmentView;
    }

    private boolean isValidInput() {
        return !ViewUtil.isEmpty(birth) && !ViewUtil.isEmpty(cell) && !ViewUtil.isEmpty(email) &&
                !ViewUtil.isEmpty(firstName) && !ViewUtil.isEmpty(lastName) && !ViewUtil.isEmpty(nickname);
    }

    @Override
    public void onClick(View v) {
        if (isValidInput()) {
            getActivity().getIntent().putExtra(User.BIRTH, birth.getText().toString());
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
