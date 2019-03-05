package com.example.rps.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rps.R
import com.example.rps.presentation.fragments.GameFragment

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
class MainActivity : AppCompatActivity(), GameFragment.OnGameFragmentInteractionListener {

    /**
     * Expected interaction types between fragments and activity.
     */
    enum class UiFragmentsMessageIdentifiers {
        UiMsgFragmentResumed;
    }

    /**
     * Used for communication between fragments and activity instead of a presenter.
     * Notice: For this project where is only one fragment and no interaction with
     * the Ui of MainActivity, it's not important.
     */
    override fun onGameFragmentInteraction(message: UiFragmentsMessageIdentifiers) {
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_pages, GameFragment(), GameFragment.TAG)
                .commit()
        }
    }

}
