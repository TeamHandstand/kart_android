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
import us.handstand.kartwheel.layout.CritInfoRelativeLayout
import us.handstand.kartwheel.layout.ViewUtil

class CriticalInfoFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {
    private lateinit var button: AppCompatButton
    private lateinit var critInfoText: TextView
    private lateinit var leftImage: ImageView
    private lateinit var rightImage: ImageView
    private lateinit var glassesSelector: CritInfoRelativeLayout
    private var pancakeOrWaffle: String? = null
    private var charmanderOrSquirtle: String? = null
    @Question private var currentQuestion: Int = FOOD
    @Answer private var selectedAnswer: Int = NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_crit_info, container, false) as ViewGroup
        glassesSelector = ViewUtil.findView(view, R.id.imageHolder)
        critInfoText = ViewUtil.findView(view, R.id.question)
        leftImage = ViewUtil.findView(view, R.id.leftImage)
        rightImage = ViewUtil.findView(view, R.id.rightImage)
        leftImage.setOnClickListener(this)
        rightImage.setOnClickListener(this)
        currentQuestion = FOOD
        selectedAnswer = NONE
        glassesSelector.resetSelection(R.color.yellow)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = (activity.findViewById(R.id.button) as AppCompatButton)
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
            glassesSelector.resetSelection(R.color.blue)
        } else if (currentQuestion == POKEMON) {
            currentQuestion = FINISHED
            ticketController.user = ticketController.user!!.construct(charmanderOrSquirtle!!, pancakeOrWaffle!!)
        }
        critInfoText.setText(if (currentQuestion == FOOD) R.string.pancakes_waffles else R.string.charmander_squirtle)
        leftImage.setImageResource(if (currentQuestion == FOOD) R.drawable.pancakes else R.drawable.charmander)
        rightImage.setImageResource(if (currentQuestion == FOOD) R.drawable.waffles else R.drawable.squirtle)

        leftImage.isSelected = selectedAnswer == LEFT
        rightImage.isSelected = selectedAnswer == RIGHT
        ticketController.onTicketFragmentStateChanged()
        return currentQuestion == FINISHED
    }

    override fun onClick(v: View) {
        if (v.id == R.id.leftImage) {
            selectedAnswer = LEFT
            if (currentQuestion == POKEMON) {
                charmanderOrSquirtle = "charmander"
            } else if (currentQuestion == FOOD) {
                pancakeOrWaffle = "pancake"
            }
            glassesSelector.leftImageTapped()
        } else if (v.id == R.id.rightImage) {
            selectedAnswer = RIGHT
            if (currentQuestion == POKEMON) {
                charmanderOrSquirtle = "squirtle"
            } else if (currentQuestion == FOOD) {
                pancakeOrWaffle = "waffle"
            }
            glassesSelector.rightImageTapped()
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
