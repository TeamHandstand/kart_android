package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.TextUtils.isEmpty
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import us.handstand.kartwheel.R


class CircularImageView : ImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setImageResource(R.drawable.placeholder_registrant_avatar_grey, R.drawable.placeholder_registrant_avatar_grey)
    }

    fun setImageUri(uri: Uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.onboarding_camera)
                .into(object : BitmapImageViewTarget(this) {
                    override fun setResource(resource: Bitmap) {
                        setImageDrawable(getScaledCircularBitmap(resource))
                    }
                })
    }

    fun setImageResource(imageRes: Int, placeholder: Int = R.drawable.background_white_circle) {
        if (imageRes == -1) {
            super.setImageResource(placeholder)
        } else {
            Glide.with(context)
                    .load(imageRes)
                    .asBitmap()
                    .fitCenter()
                    .into(object : BitmapImageViewTarget(this) {
                        override fun setResource(resource: Bitmap) {
                            setImageDrawable(getScaledCircularBitmap(resource))
                        }
                    })
        }
    }

    fun setImageUrl(imageUrl: String?, default: String = "", placeholder: Int = R.drawable.placeholder_registrant_avatar) {
        if (isEmpty(imageUrl)) {
            if (isEmpty(default)) {
                setImageResource(placeholder, placeholder)
            } else {
                setImageUrl(default, placeholder = placeholder)
            }
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .fitCenter()
                    .placeholder(placeholder)
                    .into(object : BitmapImageViewTarget(this) {
                        override fun setResource(resource: Bitmap) {
                            setImageDrawable(getScaledCircularBitmap(resource))
                        }
                    })
        }
    }

    private fun getScaledCircularBitmap(original: Bitmap): RoundedBitmapDrawable {
        val size = if (measuredHeight > 0) measuredHeight.toFloat() else if (original.width > original.height) original.height.toFloat() else original.width.toFloat()
        val scaledBitmap = Bitmap.createScaledBitmap(original, size.toInt(), size.toInt(), true)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
        circularBitmapDrawable.cornerRadius = size
        return circularBitmapDrawable
    }
}