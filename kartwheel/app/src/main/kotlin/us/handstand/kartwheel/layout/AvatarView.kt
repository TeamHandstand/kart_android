package us.handstand.kartwheel.layout

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import us.handstand.kartwheel.R

class AvatarView : RelativeLayout {
    private lateinit var profileStatusView: AvatarProfileStatusView

    //region - Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    //endregion

    //region - Initializer

    init {
        View.inflate(context, R.layout.avatar_view, this)

        profileStatusView = findViewById(R.id.profileStatusView)
        profileStatusView.setImage(R.drawable.placeholder_registrant_avatar)
    }

    //endregion
}