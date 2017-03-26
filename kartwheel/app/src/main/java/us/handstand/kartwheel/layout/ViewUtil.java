package us.handstand.kartwheel.layout;


import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ViewUtil {

    public static <T extends View> T findView(@NonNull ViewGroup viewGroup, @IdRes int id) {
        //noinspection unchecked
        return (T) viewGroup.findViewById(id);
    }

    public static <T extends View> T findView(@NonNull Activity activity, @IdRes int id) {
        //noinspection unchecked
        return (T) activity.findViewById(id);
    }

    public static boolean isEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString());
    }
}
