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

class CriticalInfoFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {
    private lateinit var button: AppCompatButton
    private lateinit var critInfoText: TextView
    private lateinit var leftImage: ImageView
    private lateinit var rightImage: ImageView
    private lateinit var glassesSelector: CritInfoRelativeLayout
    @Question private var currentQuestion: Int = FOOD
    @Answer private var selectedAnswer: Int = NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_crit_info, container, false) as ViewGroup
        glassesSelector = view.findViewById(R.id.imageHolder)
        critInfoText = view.findViewById(R.id.question)
        leftImage = view.findViewById(R.id.leftImage)
        rightImage = view.findViewById(R.id.rightImage)
        leftImage.setOnClickListener(this)
        rightImage.setOnClickListener(this)
        currentQuestion = FOOD
        selectedAnswer = NONE
        glassesSelector.nextQuestion(R.drawable.pancakes, R.drawable.waffles, R.string.pancakes_waffles, R.color.yellow)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = activity.findViewById(R.id.button)
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

    override fun getAdvanceButtonLoadingColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue_loading else super.getAdvanceButtonLoadingColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return selectedAnswer != NONE
    }

    override fun canAdvanceToNextStep(): Boolean {
        if (currentQuestion == FOOD) {
            ticketController.pancakeOrWaffle = if (selectedAnswer == LEFT) "pancake" else "waffle"
            glassesSelector.nextQuestion(R.drawable.charmander, R.drawable.squirtle, R.string.charmander_squirtle, R.color.blue)
            currentQuestion = POKEMON
            selectedAnswer = NONE
        } else if (currentQuestion == POKEMON) {
            ticketController.charmanderOrSquirtle = if (selectedAnswer == LEFT) "charmander" else "squirtle"
            glassesSelector.nextQuestion(R.drawable.furby, R.drawable.tamagachi, R.string.furby_tamagachi, R.color.red)
            currentQuestion = NINETIES
            selectedAnswer = NONE
        } else if (currentQuestion == NINETIES) {
            ticketController.furbyOrTamagachi = if (selectedAnswer == LEFT) "furby" else "tamagachi"
            currentQuestion = FINISHED
        }

        leftImage.isSelected = selectedAnswer == LEFT
        rightImage.isSelected = selectedAnswer == RIGHT
        ticketController.onTicketFragmentStateChanged()
        return currentQuestion == FINISHED
    }

    override fun onClick(v: View) {
        if (v.id == R.id.leftImage) {
            selectedAnswer = LEFT
            glassesSelector.leftImageTapped()
        } else if (v.id == R.id.rightImage) {
            selectedAnswer = RIGHT
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
        const val NINETIES = 2
        const val FINISHED = 3

        @IntDef(NONE.toLong(), LEFT.toLong(), RIGHT.toLong())
        annotation class Answer

        @IntDef(FOOD.toLong(), POKEMON.toLong(), FINISHED.toLong())
        annotation class Question
    }
}
