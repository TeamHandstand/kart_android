package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import com.bumptech.glide.request.RequestOptions

class SimpleGlideImageView : GlideImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getImageRequestOptions(): RequestOptions = RequestOptions.fitCenterTransform()
}
