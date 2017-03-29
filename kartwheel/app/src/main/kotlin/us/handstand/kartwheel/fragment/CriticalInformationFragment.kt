package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.UserModel

class CriticalInformationFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {

    internal var critInfoText: TextView? = null
    internal var leftImage: ImageView? = null
    internal var rightImage: ImageView? = null
    @Question private var currentQuestion: Int = 0
    @Answer private var selectedAnswer: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_crit_info, container, false) as ViewGroup
        critInfoText = ViewUtil.findView(view, R.id.critical_information_question)
        leftImage = ViewUtil.findView(view, R.id.left_image)
        rightImage = ViewUtil.findView(view, R.id.right_image)
        leftImage!!.setOnClickListener(this)
        rightImage!!.setOnClickListener(this)
        currentQuestion = FOOD
        selectedAnswer = NONE
        return view
    }

    override fun onResume() {
        super.onResume()
        updateState()
    }

    private fun updateState() {
        currentQuestion = if (activity.intent.hasExtra(UserModel.PANCAKEORWAFFLE)) POKEMON else FOOD
        critInfoText!!.setText(if (currentQuestion == FOOD) R.string.pancakes_waffles else R.string.charmander_squirtle)
        leftImage!!.setImageResource(if (currentQuestion == FOOD) R.mipmap.pancakes else R.mipmap.charmander)
        rightImage!!.setImageResource(if (currentQuestion == FOOD) R.mipmap.waffles else R.mipmap.squirtle)

        leftImage!!.isSelected = selectedAnswer == LEFT
        rightImage!!.isSelected = selectedAnswer == RIGHT
        (activity as TicketActivity).setButtonState(if (selectedAnswer == NONE) R.color.grey_button_disabled else R.color.blue, R.string.next, selectedAnswer != NONE)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.left_image || v.id == R.id.right_image) {
            selectedAnswer = if (v.id == R.id.left_image) LEFT else RIGHT
            (activity as TicketActivity).setButtonState(R.color.blue, R.string.next, true)
        } else if (selectedAnswer != NONE) {
            activity.intent.putExtra(if (currentQuestion == FOOD) UserModel.PANCAKEORWAFFLE else UserModel.CHARMANDERORSQUIRTLE, selectedAnswer)
            currentQuestion = if (currentQuestion == FOOD) POKEMON else FINISHED
            selectedAnswer = NONE
            updateState()
        }
    }

    companion object {
        const val NONE = -1
        const val LEFT = 0
        const val RIGHT = 1

        const val FOOD = 0
        const val POKEMON = 1
        const val FINISHED = 2

        @IntDef(NONE.toLong(), LEFT.toLong(), RIGHT.toLong())
        annotation class Answer

        @IntDef(FOOD.toLong(), POKEMON.toLong(), FINISHED.toLong())
        annotation class Question
    }
}
