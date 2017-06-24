package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.AttributeSet
import us.handstand.kartwheel.R


class CircularImageView : GlideImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setImageResource(R.drawable.placeholder_registrant_avatar_grey, R.drawable.placeholder_registrant_avatar_grey)
    }

    override fun toDrawable(original: Bitmap): Drawable {
        val size = if (measuredHeight > 0) measuredHeight.toFloat() else if (original.width > original.height) original.height.toFloat() else original.width.toFloat()
        val scaledBitmap = Bitmap.createScaledBitmap(original, size.toInt(), size.toInt(), true)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
        circularBitmapDrawable.cornerRadius = size
        return circularBitmapDrawable
    }
}