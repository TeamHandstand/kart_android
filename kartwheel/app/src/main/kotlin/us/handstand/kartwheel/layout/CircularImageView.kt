package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import us.handstand.kartwheel.R


class RegistrantAvatarView : ImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setImageResource(R.drawable.placeholder_registrant_avatar_grey)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (measuredHeight == 0) {
            waitForMeasuredHeight { setImageBitmap(bm) }
        } else {
            val ratio = measuredWidth / measuredHeight.toFloat()
            Glide.with(context)
                    .load(bm)
                    .asBitmap()
                    .fitCenter()
                    .placeholder(R.drawable.background_white_circle)
                    .into(object : BitmapImageViewTarget(this) {
                        override fun setResource(resource: Bitmap) {
                            setImageDrawable(getScaledCircularBitmap(ratio, resource))
                        }
                    })
        }
    }

    override fun setImageResource(imageRes: Int) {
        if (imageRes == -1) {
            super.setImageResource(R.drawable.background_white_circle)
        } else {
            if (measuredHeight == 0) {
                waitForMeasuredHeight { setImageResource(imageRes) }
            } else {
                val ratio = measuredWidth / measuredHeight.toFloat()
                Glide.with(context)
                        .load(imageRes)
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.background_white_circle)
                        .into(object : BitmapImageViewTarget(this) {
                            override fun setResource(resource: Bitmap) {
                                setImageDrawable(getScaledCircularBitmap(ratio, resource))
                            }
                        })
            }

        }
    }

    fun setRegistrantImageUrl(imageUrl: String) {
        if (imageUrl == "") {
            setImageResource(R.drawable.placeholder_registrant_avatar)
        } else {
            if (measuredHeight == 0) {
                waitForMeasuredHeight { setRegistrantImageUrl(imageUrl) }
            } else {
                Glide.with(context)
                        .load(imageUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_registrant_avatar)
                        .into(object : BitmapImageViewTarget(this) {
                            override fun setResource(resource: Bitmap) {
                                setImageDrawable(getScaledCircularBitmap(original = resource))
                            }
                        })
            }
        }
    }

    fun getScaledCircularBitmap(ratio: Float = 1f, original: Bitmap): RoundedBitmapDrawable {
        val scaledBitmap = Bitmap.createScaledBitmap(original, (original.width * ratio).toInt(), (original.height * ratio).toInt(), true)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
        circularBitmapDrawable.isCircular = true
        return circularBitmapDrawable
    }

    fun waitForMeasuredHeight(futureUnit: () -> Unit) {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                futureUnit.invoke()
                return true
            }
        })
    }
}