package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.TextUtils.isEmpty
import android.util.AttributeSet
import android.view.ViewTreeObserver
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
                        setImageDrawable(getScaledCircularBitmap(original = resource))
                    }
                })
    }

    fun setImageResource(imageRes: Int, placeholder: Int = R.drawable.background_white_circle) {
        if (imageRes == -1) {
            super.setImageResource(placeholder)
        } else {
//            val height = measuredHeight.toFloat()
//            val ratio = measuredWidth / (if (height == 0f) 1f else height)
            Glide.with(context)
                    .load(imageRes)
                    .asBitmap()
                    .fitCenter()
                    .placeholder(placeholder)
                    .into(object : BitmapImageViewTarget(this) {
                        override fun setResource(resource: Bitmap) {
                            setImageDrawable(getScaledCircularBitmap(1f, resource))
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
                            setImageDrawable(getScaledCircularBitmap(original = resource))
                        }
                    })
        }
    }

    private fun getScaledCircularBitmap(ratio: Float = 1f, original: Bitmap): RoundedBitmapDrawable {
        val scaledWidth = original.width * ratio
        val scaledHeight = original.height * ratio
        val size = if (measuredHeight > 0) measuredHeight.toFloat() else if (scaledWidth > scaledHeight) scaledHeight else scaledWidth
        val scaledBitmap = Bitmap.createScaledBitmap(original, size.toInt(), size.toInt(), true)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
        circularBitmapDrawable.cornerRadius = size
        return circularBitmapDrawable
    }

    private fun waitForMeasuredHeight(futureUnit: () -> Unit) {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                futureUnit.invoke()
                return true
            }
        })
    }
}