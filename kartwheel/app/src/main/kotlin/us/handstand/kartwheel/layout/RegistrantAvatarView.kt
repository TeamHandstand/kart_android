package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
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

    override fun setImageResource(imageRes: Int) {
        if (imageRes == -1) {
            super.setImageResource(R.drawable.background_white_circle)
        } else {
            if (measuredHeight == 0) {
                viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        viewTreeObserver.removeOnPreDrawListener(this)
                        setImageResource(imageRes)
                        return true
                    }
                })
            } else {
                val ratio = measuredWidth / measuredHeight
                Glide.with(context)
                        .load(imageRes)
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.background_white_circle)
                        .into(object : BitmapImageViewTarget(this) {
                            override fun setResource(resource: Bitmap) {
                                val scaledBitmap = Bitmap.createScaledBitmap(resource, resource.width * ratio, resource.height * ratio, true)
                                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
                                circularBitmapDrawable.isCircular = true
                                setImageDrawable(circularBitmapDrawable)
                            }
                        })
            }

        }
    }

    fun setRegistrantImageUrl(imageUrl: String) {
        if (imageUrl == "") {
            setImageResource(R.drawable.placeholder_registrant_avatar)
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_registrant_avatar)
                    .into(object : BitmapImageViewTarget(this) {
                        override fun setResource(resource: Bitmap?) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                            circularBitmapDrawable.isCircular = true
                            setImageDrawable(circularBitmapDrawable)
                        }
                    })
        }
    }
}