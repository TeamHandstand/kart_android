package us.handstand.kartwheel.layout

import android.content.Context
import android.net.Uri
import android.text.TextUtils.isEmpty
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import us.handstand.kartwheel.R


abstract class GlideImageView : ImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    abstract fun getImageRequestOptions(): RequestOptions

    init {
        if (!isInEditMode) {
            super.setImageResource(R.drawable.placeholder_registrant_avatar_grey)
        }
    }

    fun setImageUri(uri: Uri) {
        GlideApp.with(context)
                .load(uri)
                .apply(getImageRequestOptions())
                .centerCrop()
                .placeholder(R.drawable.onboarding_camera)
                .into(this)
    }

    fun setImageResource(imageRes: Int, placeholder: Int = R.drawable.background_white_circle) {
        if (imageRes == -1) {
            super.setImageResource(placeholder)
        } else {
            GlideApp.with(context)
                    .load(imageRes)
                    .fitCenter()
                    .apply(getImageRequestOptions())
                    .into(this)
        }
    }

    fun setImageUrl(imageUrl: String?, default: String = "", placeholder: Int = R.drawable.placeholder_registrant_avatar, crop: Boolean = true) {
        if (isEmpty(imageUrl)) {
            if (isEmpty(default)) {
                GlideApp.with(context).load(placeholder).fitCenter().into(this)
            } else {
                setImageUrl(default, placeholder = placeholder)
            }
        } else {
            if (crop) {
                GlideApp.with(context)
                        .load(imageUrl)
                        .apply(getImageRequestOptions())
                        .placeholder(placeholder)
                        .into(this)
            } else {
                GlideApp.with(context)
                        .load(imageUrl)
                        .fitCenter()
                        .placeholder(placeholder)
                        .into(this)
            }
        }
    }
}
