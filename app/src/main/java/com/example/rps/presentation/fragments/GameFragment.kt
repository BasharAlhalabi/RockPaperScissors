package com.example.rps.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.rps.R
import com.example.rps.logic.callbacks.OnSwipeTouchListener
import com.example.rps.logic.structure.GameMove
import com.example.rps.logic.structure.GamePlayer
import com.example.rps.presentation.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_game.*

private const val ARG_PARAM1 = "param1"

/**
 * Game: Rock Paper Scissor
 *
 * Controls:
 *      Clicking on the right view (hand image) will change the move selection.
 *      Swiping left on the right view (hand image) submits the move.
 *
 * Notice: The left view (hand image) has new random selection for every round.
 * Notice: Used images are edited from (https://tenor.com/view/rock-paper-scissors-games-gif-4954978)
 *
 * Created by Bashar Al Halabi on 03.03.19.
 * @author Bashar Al Halabi
 */
class GameFragment : Fragment() {

    private var mStartingParam: String? = null
    private var mListener: OnGameFragmentInteractionListener? = null

    //savedInstanceState keys
    val PLAYING_STATUS_KEY = "playingStatus"
    val HUMAN_MOVE_KEY = "humanMove"
    val AUTO_MOVE_KEY = "autoMove"
    val RESULT_TEXT_KEY = "initResultText"

    companion object {
        val TAG = GameFragment::class.java.name
    }

    interface OnGameFragmentInteractionListener {
        fun onGameFragmentInteraction(message: MainActivity.UiFragmentsMessageIdentifiers)
    }

    /////////////////Life cycle related//////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _communicateIn()

        if (savedInstanceState != null) {
            playingStatus = savedInstanceState.getInt(PLAYING_STATUS_KEY)
            humanMove = GameMove.values()[savedInstanceState.getInt(HUMAN_MOVE_KEY)]
            autoMove = GameMove.values()[savedInstanceState.getInt(AUTO_MOVE_KEY)]
            if (savedInstanceState.containsKey(RESULT_TEXT_KEY)) {
                initResultText = savedInstanceState.getString(RESULT_TEXT_KEY)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGameFragmentInteractionListener) mListener = context
    }

    override fun onResume() {
        super.onResume()
        _communicateOut(MainActivity.UiFragmentsMessageIdentifiers.UiMsgFragmentResumed)
        _init_game()
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {

        with(outState) {
            putInt(PLAYING_STATUS_KEY, playingStatus)
            putInt(HUMAN_MOVE_KEY, humanMove.order)
            putInt(AUTO_MOVE_KEY, autoMove.order)
            if (tv_turn_indicator.isShown)
                putString(RESULT_TEXT_KEY, tv_turn_indicator.text.toString())
        }

        super.onSaveInstanceState(outState)
    }

    /////////////////UI communication//////////////////////

    private fun _communicateOut(message: MainActivity.UiFragmentsMessageIdentifiers) {
        if (mListener != null) {
            mListener!!.onGameFragmentInteraction(message)
        }
    }

    private fun _communicateIn() {
        mStartingParam = arguments?.getString(ARG_PARAM1)
    }

    ////////////// Logic ///////////////////////////////////

    /**
     * Hold the current moves of players players
     */
    private var humanMove: GameMove = GameMove.ROCK
    private var autoMove: GameMove = GameMove.ROCK

    /**
     * Holds the count of players played per round
     */
    private var playingStatus = 0
    private var initResultText: String? = null

    private fun _init_game() {

        when (humanMove) {
            GameMove.ROCK -> iv_player_human.setImageResource(R.drawable.rock)
            GameMove.PAPER -> iv_player_human.setImageResource(R.drawable.paper)
            GameMove.SCISSORS -> iv_player_human.setImageResource(R.drawable.scissor)
        }

        when (autoMove) {
            GameMove.ROCK -> iv_player_auto.setImageResource(R.drawable.rock)
            GameMove.PAPER -> iv_player_auto.setImageResource(R.drawable.paper)
            GameMove.SCISSORS -> iv_player_auto.setImageResource(R.drawable.scissor)
        }

        if (initResultText != null)
            tv_turn_indicator.let {
                it.visibility = View.VISIBLE
                it.text = initResultText
            }

        iv_player_human.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeLeft() {
                _submitMoveAnimated(humanMove, GamePlayer.Human)
                _submitMoveAnimated(autoMove.next(true), GamePlayer.Auto)
            }

            override fun onClick() {
                _switchMove(humanMove.next(false), GamePlayer.Human, false)
            }
        })
    }

    private fun _switchMove(gameMove: GameMove, player: GamePlayer, submitMove: Boolean) {

        val imageView: ImageView = when (player) {
            GamePlayer.Human -> iv_player_human
            GamePlayer.Auto -> iv_player_auto
        }

        when (gameMove) {
            GameMove.ROCK -> imageView.setImageResource(R.drawable.rock)
            GameMove.PAPER -> imageView.setImageResource(R.drawable.paper)
            GameMove.SCISSORS -> imageView.setImageResource(R.drawable.scissor)
        }

        when (player) {
            GamePlayer.Human -> humanMove = gameMove
            GamePlayer.Auto -> autoMove = gameMove
        }

        if (submitMove) {
            playingStatus++
            _decideResult()
        }
    }

    private fun _submitMoveAnimated(gameMove: GameMove, player: GamePlayer) {

        val imageView: ImageView = when (player) {
            GamePlayer.Human -> iv_player_human
            GamePlayer.Auto -> iv_player_auto
        }

        val pivotX = when (player) {
            GamePlayer.Human -> 1.0f
            GamePlayer.Auto -> 0.0f
        }

        val toDegrees = when (player) {
            GamePlayer.Human -> 15f
            GamePlayer.Auto -> -15f
        }

        val toDeltaX = when (player) {
            GamePlayer.Human -> 150f
            GamePlayer.Auto -> -150f
        }

        val rotateAnim = RotateAnimation(
            0f, toDegrees, Animation.RELATIVE_TO_SELF,
            pivotX, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.interpolator = AccelerateDecelerateInterpolator()

        val translateAnim = TranslateAnimation(
            0f, toDeltaX,
            0f, 0f
        )

        val animationSet = AnimationSet(true)
        animationSet.let {
            it.addAnimation(rotateAnim)
            it.addAnimation(translateAnim)
            it.duration = 500
        }

        imageView.let {
            it.clearAnimation()
            it.animation = animationSet
            it.animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                    //do nothing
                }

                override fun onAnimationEnd(animation: Animation?) {
                    _switchMove(gameMove, player, true)
                }

                override fun onAnimationStart(animation: Animation?) {
                    sbv_text_presenter.visibility = View.GONE
                    imageView.setImageResource(R.drawable.rock)
                }
            })
            it.animation.start()
        }

    }

    /**
     * Checks if both players has played, then shows the round result
     */
    private fun _decideResult() {

        if (playingStatus == 2) {

            tv_turn_indicator?.text = when {
                humanMove == autoMove -> getString(R.string.text_result_draw)
                humanMove.hits(autoMove) -> getString(R.string.text_result_win)
                else -> getString(R.string.text_result_lose)
            }

            sbv_text_presenter.let {
                it.likeAnimation()
                it.visibility = View.VISIBLE
            }

            playingStatus = 0
        }
    }

}
