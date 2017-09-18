package us.handstand.kartwheel.layout;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KartTextView extends AppCompatTextView {
    public KartTextView(@NotNull Context context) {
        super(context);
    }

    public KartTextView(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        KartFontView.INSTANCE.setLineSpacingFromAttrs(this, context, attrs);
        if (!isInEditMode()) {
            KartFontView.INSTANCE.setTypefaceFromAttrs(this, context, attrs);
        }

    }

    public KartTextView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        KartFontView.INSTANCE.setLineSpacingFromAttrs(this, context, attrs);
        if (!isInEditMode()) {
            KartFontView.INSTANCE.setTypefaceFromAttrs(this, context, attrs);
        }

    }
}
