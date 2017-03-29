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
        View.inflate(context, R.layout.item_view, this)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ItemView, 0, 0)
        try {
            (findViewById(R.id.image) as ImageView).setImageResource(a.getResourceId(R.styleable.ItemView_src, R.mipmap.red_turtle))
            (findViewById(R.id.title) as TextView).text = a.getString(R.styleable.ItemView_title)
            (findViewById(R.id.description) as TextView).text = a.getString(R.styleable.ItemView_description)
        } finally {
            a.recycle()
        }
    }
}


