package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.layout.ViewUtil;

public class CodeEntryFragment extends Fragment implements TicketActivity.TicketFragment {

    public static final String BUNDLE_CODE_KEY = "bundle_code_key";
    private EditText codeEntry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup fragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_code_entry, container, false);
        codeEntry = ViewUtil.findView(fragmentView, R.id.code_edit_text);
        return fragmentView;
    }

    @Nullable
    @Override
    public Bundle onButtonClicked(@IdRes int id) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CODE_KEY, codeEntry.getText().toString().toLowerCase());
        return bundle;
    }
}
