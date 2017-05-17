package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.ViewUtil

class CriticalInfoFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {
    internal var critInfoText: TextView? = null

    internal var leftImage: ImageView? = null
    internal var rightImage: ImageView? = null
    internal var button: AppCompatButton? = null
    private var pancakeOrWaffle: String? = null
    private var charmanderOrSquirtle: String? = null
    @Question private var currentQuestion: Int = FOOD
    @Answer private var selectedAnswer: Int = NONE

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = ViewUtil.findView(activity, R.id.button)
    }

    override fun getTitleResId(): Int {
        return R.string.welcome
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.next
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return selectedAnswer != NONE
    }

    override fun canAdvanceToNextStep(): Boolean {
        if (currentQuestion == FOOD) {
            currentQuestion = POKEMON
            selectedAnswer = NONE
        } else if (currentQuestion == POKEMON) {
            currentQuestion = FINISHED
            ticketController.user = ticketController.user!!.construct(charmanderOrSquirtle!!, pancakeOrWaffle!!)
        }
        critInfoText!!.setText(if (currentQuestion == FOOD) R.string.pancakes_waffles else R.string.charmander_squirtle)
        leftImage!!.setImageResource(if (currentQuestion == FOOD) R.mipmap.pancakes else R.mipmap.charmander)
        rightImage!!.setImageResource(if (currentQuestion == FOOD) R.mipmap.waffles else R.mipmap.squirtle)

        leftImage!!.isSelected = selectedAnswer == LEFT
        rightImage!!.isSelected = selectedAnswer == RIGHT
        ticketController.onTicketFragmentStateChanged()
        return currentQuestion == FINISHED
    }

    override fun onClick(v: View) {
        if (v.id == R.id.left_image) {
            selectedAnswer = LEFT
            if (currentQuestion == POKEMON) {
                charmanderOrSquirtle = "charmander"
            } else if (currentQuestion == FOOD) {
                pancakeOrWaffle = "pancake"
            }
        } else if (v.id == R.id.right_image) {
            selectedAnswer = RIGHT
            if (currentQuestion == POKEMON) {
                charmanderOrSquirtle = "squirtle"
            } else if (currentQuestion == FOOD) {
                pancakeOrWaffle = "waffle"
            }
        }

        ticketController.onTicketFragmentStateChanged()
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
