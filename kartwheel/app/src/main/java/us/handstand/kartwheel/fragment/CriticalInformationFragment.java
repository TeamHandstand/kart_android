package us.handstand.kartwheel.fragment;


import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import us.handstand.kartwheel.R;
import us.handstand.kartwheel.activity.TicketActivity;
import us.handstand.kartwheel.layout.ViewUtil;
import us.handstand.kartwheel.model.User;

public class CriticalInformationFragment extends Fragment implements TicketActivity.TicketFragment, View.OnClickListener {

    private TextView critInfoText;
    private ImageView leftImage;
    private ImageView rightImage;
    @Question
    private int currentQuestion;
    @Answer
    private int selectedAnswer;

    @IntDef({NONE, LEFT, RIGHT})
    public @interface Answer {
    }

    public static final int NONE = -1;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    @IntDef({FOOD, POKEMON, FINISHED})
    public @interface Question {
    }

    public static final int FOOD = 0;
    public static final int POKEMON = 1;
    public static final int FINISHED = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup fragmentViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_crit_info, container, false);
        critInfoText = ViewUtil.findView(fragmentViewGroup, R.id.critical_information_question);
        leftImage = ViewUtil.findView(fragmentViewGroup, R.id.left_image);
        leftImage.setOnClickListener(this);
        rightImage = ViewUtil.findView(fragmentViewGroup, R.id.right_image);
        rightImage.setOnClickListener(this);
        currentQuestion = FOOD;
        selectedAnswer = NONE;
        return fragmentViewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    private void updateState() {
        currentQuestion = getActivity().getIntent().hasExtra(User.PANCAKEORWAFFLE) ? POKEMON : FOOD;
        critInfoText.setText(currentQuestion == FOOD ? R.string.pancakes_waffles : R.string.charmander_squirtle);
        leftImage.setImageResource(currentQuestion == FOOD ? R.mipmap.pancakes : R.mipmap.charmander);
        rightImage.setImageResource(currentQuestion == FOOD ? R.mipmap.waffles : R.mipmap.squirtle);

        leftImage.setSelected(selectedAnswer == LEFT);
        rightImage.setSelected(selectedAnswer == RIGHT);
        ((TicketActivity) getActivity()).setButtonState(selectedAnswer == NONE ? R.color.grey_button_disabled : R.color.blue, R.string.next, selectedAnswer != NONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_image || v.getId() == R.id.right_image) {
            selectedAnswer = (v.getId() == R.id.left_image ? LEFT : RIGHT);
            ((TicketActivity) getActivity()).setButtonState(R.color.blue, R.string.next, true);
        } else if (selectedAnswer != NONE) {
            getActivity().getIntent().putExtra(currentQuestion == FOOD ? User.PANCAKEORWAFFLE : User.CHARMANDERORSQUIRTLE, selectedAnswer);
            currentQuestion = (currentQuestion == FOOD ? POKEMON : FINISHED);
            selectedAnswer = NONE;
            updateState();
        }
    }
}
