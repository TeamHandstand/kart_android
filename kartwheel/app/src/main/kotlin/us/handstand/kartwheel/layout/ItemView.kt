package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R


class ItemView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_info_item, this)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ItemView, 0, 0)
        try {
            findViewById<ImageView>(R.id.image).setImageResource(a.getResourceId(R.styleable.ItemView_src, R.drawable.race_item_red_shell))
            findViewById<TextView>(R.id.title).text = a.getString(R.styleable.ItemView_title)
            findViewById<TextView>(R.id.description).text = a.getString(R.styleable.ItemView_description)
        } finally {
            a.recycle()
        }
    }
}


