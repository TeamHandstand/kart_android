package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import com.bumptech.glide.request.RequestOptions
import us.handstand.kartwheel.R


class CircularImageView : GlideImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        if (!isInEditMode) {
            setImageResource(R.drawable.placeholder_registrant_avatar_grey)
        }
    }

    override fun getImageRequestOptions(): RequestOptions = RequestOptions.circleCropTransform()
}
