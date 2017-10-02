package us.handstand.kartwheel.layout

import android.content.Context
import android.net.Uri
import android.text.TextUtils.isEmpty
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import us.handstand.kartwheel.R


class CircularImageView : ImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        if (!isInEditMode) {
            setImageResource(R.drawable.placeholder_registrant_avatar_grey)
        }
    }

    fun setImageUrl(imageUrl: String?, placeholder: Int = R.drawable.placeholder_registrant_avatar, crop: Boolean = true) {
        if (isEmpty(imageUrl)) {
            setCircularImageResource(placeholder, false)
        } else {
            GlideApp.with(context)
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .apply(if (crop) RequestOptions.circleCropTransform() else RequestOptions().fitCenter())
                    .into(this)
        }
    }

    fun setImageUri(uri: Uri, placeholder: Int = R.drawable.onboarding_camera) {
        GlideApp.with(context)
                .load(uri)
                .placeholder(placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(this)
    }

    fun setCircularImageResource(imageRes: Int, crop: Boolean = true) {
        GlideApp.with(context)
                .load(imageRes)
                .apply(if (crop) RequestOptions.circleCropTransform() else RequestOptions().fitCenter())
                .into(this)
    }
}
