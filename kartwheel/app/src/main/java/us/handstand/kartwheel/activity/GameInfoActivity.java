package us.handstand.kartwheel.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class GameInfoActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, GameInfoActivity.class);
    }
}
