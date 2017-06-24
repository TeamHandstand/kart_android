package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet

class SimpleGlideImageView : GlideImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun toDrawable(original: Bitmap): Drawable {
        return BitmapDrawable(resources, original)
    }
}
