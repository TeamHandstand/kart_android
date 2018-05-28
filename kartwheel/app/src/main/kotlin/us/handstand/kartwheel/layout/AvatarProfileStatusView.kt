package us.handstand.kartwheel.layout

import android.content.Context
import us.handstand.kartwheel.R
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View

class AvatarProfileStatusView : CardView {
    private lateinit var profileImageView: CircularImageView

    init {
        View.inflate(context, R.layout.avatar_profile_status_view, this)

        profileImageView = findViewById(R.id.profileImageView)
    }

    //region - Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs:AttributeSet) : super(context, attrs)

    //endregion

    //region - Public

    fun setImage(drawableId: Int) {
        profileImageView.setImageResource(R.drawable.placeholder_registrant_avatar)
    }

    fun setImage(imageUrl: String) {
        profileImageView.setImageUrl(imageUrl, placeholder = R.drawable.placeholder_registrant_avatar)
    }

    //endregion
}