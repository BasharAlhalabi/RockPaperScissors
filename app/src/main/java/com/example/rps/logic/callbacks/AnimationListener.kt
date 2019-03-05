package com.example.rps.logic.callbacks

import com.example.rps.logic.structure.GameMove
import com.example.rps.logic.structure.GamePlayer

interface AnimationListener
{
    fun onAnimationEnd(gameMove: GameMove, player: GamePlayer)
    fun onAnimationStrat(gameMove: GameMove, player: GamePlayer)
}