package com.example.rps.logic.structure

enum class GameMove(val order: Int) {
    ROCK(0),
    PAPER(1),
    SCISSORS(2);

    /**
     * Indicates the stronger move
     */
    fun hits(other: GameMove): Boolean {
        return (this.order - other.order + GameMove.values().size) % GameMove.values().size == 1
    }

    /**
     * Returns the next move:
     *      If random == false --> w.r.t. order.
     *      If random == true --> random selection.
     */
    fun next(random: Boolean): GameMove {
        var nextIndex = 1
        if (random)
            nextIndex = (1..9).shuffled().first()
        return GameMove.values()[(this.order + nextIndex) % GameMove.values().size]
    }
}