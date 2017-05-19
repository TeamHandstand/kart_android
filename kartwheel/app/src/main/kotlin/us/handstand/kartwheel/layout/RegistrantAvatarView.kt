package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.User


class RegistrantAvatarView : ImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setImageResource(R.drawable.placeholder_registrant_avatar_grey)
    }

    fun setRegistrant(registrant: User) {
        if (registrant.id() == "") {
            setImageResource(R.drawable.placeholder_registrant_avatar)
        } else {
            Glide.with(context)
                    .load(registrant.imageUrl())
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