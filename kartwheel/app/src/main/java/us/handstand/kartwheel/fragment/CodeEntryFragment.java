package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.model.Ticket;

public class CodeEntryFragment extends Fragment implements TicketActivity.TicketFragment {

    @BindView(R.id.code_edit_text)
    EditText codeEntry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_code_entry, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        getActivity().getIntent().putExtra(Ticket.CODE, codeEntry.getText().toString().toLowerCase().replace(" ", ""));
    }
}
