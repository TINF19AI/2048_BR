package com.dhbw.br2048.data

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import androidx.core.animation.doOnEnd
import com.dhbw.br2048.presentation.TileView

class GameManager(
    private val context: Context,
    private val gridLayout: GridLayout,
    private val gridSize: Coordinates,
    private val startTiles: Int,
) {
    private val grid = Grid(gridSize)

    var wonCallback: ((Int) -> Unit)? = null
    private var won = false
        set(newValue) {
            field = newValue
            wonCallback?.let { it(score) }
        }

    var overCallback: ((Int) -> Unit)? = null
    private var over = false
        set(newValue) {
            field = newValue
            overCallback?.let { it(score) }
        }

    var scoreCallback: ((Int) -> Unit)? = null
    private var score = 0
        set(newValue) {
            field = newValue
            scoreCallback?.let { it(score) }
        }

    // Set up the initial tiles to start the game with
    // From open source 2048, ported by Maxi
    fun addStartTiles() {
        for (i in 1..startTiles) {
            this.addRandomTile()
        }
    }

    // Adds a tile in a random position
    // From open source 2048, ported by Maxi
    private fun addRandomTile() {
        if (grid.cellsAvailable()) {
            val value = if (Math.random() < 0.9) 2 else 4
            val tile =
                grid.randomAvailableCell()?.let { TileView(context, pos = it, startValue = value) }
            if (tile != null) {
                grid.insertTile(tile)
                tile.setGridLayout(gridLayout)
            }
        }
    }

    // Author: Kai
    fun setTile(value: Int, pos: Coordinates) {
        val tile = grid.cellContent(pos)
        tile?.let {
            it.value = value
        } ?: run { // if null
            val newTile = TileView(context, pos = pos, startValue = value)
            grid.insertTile(newTile)
            newTile.setGridLayout(gridLayout, doAppearAnim = false)
        }
    }

    // Move a tile and its representation
    // From open source 2048, ported by Maxi
    private fun moveTile(tile: TileView, cell: Coordinates) {
        grid.move(tile, cell)
    }

    // Get the vector representing the chosen direction
    // From open source 2048, ported by Maxi
    private fun getVector(direction: Direction): Coordinates? {
        // Vectors representing tile movement
        val map = mapOf(
            0 to Coordinates(0, -1),
            1 to Coordinates(1, 0),
            2 to Coordinates(0, 1),
            3 to Coordinates(-1, 0)
        )
        return map[direction.direction]
    }

    // Build a list of positions to traverse in the right order
    // From open source 2048, ported by Maxi
    private fun buildTraversals(vector: Coordinates): Pair<MutableList<Int>, MutableList<Int>> {
        val traversalsX = mutableListOf<Int>()
        val traversalsY = mutableListOf<Int>()

        // @todo supports only square grid
        for (pos in 0 until gridSize.x) {
            traversalsX.add(pos)
            traversalsY.add(pos)
        }

        // Always traverse from the farthest cell in the chosen direction
        if (vector.x == 1) traversalsX.reverse()
        if (vector.y == 1) traversalsY.reverse()

        return Pair(traversalsX, traversalsY)
    }

    // Move tiles on the grid in the specified direction
    // From open source 2048, ported by Maxi
    fun move(direction: Direction) {
        // 0: up, 1: right, 2: down, 3: left

        //if (this.isGameTerminated()) return; // Don't do anything if the game's over

        var cell: Coordinates
        var tile: TileView?

        val vector = getVector(direction) ?: return
        val traversals = buildTraversals(vector)
        var moved = false

        // Author move/mergeAnimations: Kai
        val moveAnimations = mutableSetOf<Animator>()
        val mergeAnimations = mutableSetOf<Animator>()

        // Traverse the grid in the right direction and move tiles
        for (x in traversals.first) {
            for (y in traversals.second) {

                cell = Coordinates(x, y)
                tile = grid.cellContent(cell)

                tile?.let {
                    val positions = findFarthestPosition(cell, vector)
                    val next = grid.cellContent(positions.second)

                    // Only one merger per row traversal?
                    if (next != null && next.value == tile.value && next.mergedFrom == null) {
                        moveAnimations.add(next.moveToAnimation(positions.second))
                        moveAnimations.add(tile.moveToAnimation(positions.second))
                        mergeAnimations.add(next.mergeAnimation(tile))

                        next.value = tile.value * 2
                        next.coordinates = positions.second
                        next.mergedFrom = arrayOf(tile, next)

                        // Replace old tile
                        grid.removeTile(tile)

                        moved = true

                        // Update the score
                        score += next.value

                        // The mighty 2048 tile
                        if (next.value == 2048) won = true
                    } else {
                        moveAnimations.add(tile.moveToAnimation(positions.first))
                        moveTile(tile, positions.first)
                    }

                    if (!cell.isEqual(tile.coordinates)) {
                        moved = true // The tile moved from its original cell!
                    }
                }
            }
        }

        if (moved) {
            // Author: Kai
            val mergeAniSet = AnimatorSet()
            mergeAniSet.playTogether(mergeAnimations)
            mergeAniSet.interpolator = DecelerateInterpolator()
            mergeAniSet.duration = 200

            val moveAniSet = AnimatorSet()
            moveAniSet.playTogether(moveAnimations)
            moveAniSet.interpolator = AccelerateInterpolator()
            moveAniSet.duration = 75
            moveAniSet.doOnEnd {
                mergeAniSet.start()
            }

            moveAniSet.start()
            // End Kai

            this.addRandomTile()

            if (!movesAvailable()) {
                over = true // Game over!
            }

            grid.eachCell { _: Coordinates, tileView: TileView? ->
                if (tileView != null) {
                    tileView.mergedFrom = null
                }
            }
        }
    }

    // From open source 2048, ported by Maxi
    private fun findFarthestPosition(
        cell: Coordinates,
        vector: Coordinates
    ): Pair<Coordinates, Coordinates> {
        var previous: Coordinates
        var newCell = cell

        // Progress towards the vector direction until an obstacle is found
        do {
            previous = newCell
            newCell = Coordinates(previous.x + vector.x, previous.y + vector.y)
        } while (
            grid.withinBounds(newCell) &&
            grid.cellAvailable(newCell)
        )

        return Pair(previous, newCell)// Used to check if a merge is required

    }

    // From open source 2048, ported by Maxi
    private fun movesAvailable(): Boolean {
        return grid.cellsAvailable() || tileMatchesAvailable()
    }

    // Check for available matches between tiles (more expensive check)
    // From open source 2048, ported by Maxi
    private fun tileMatchesAvailable(): Boolean {
        //var tile: TileView;
        var result = false

        grid.eachCell { coordinates: Coordinates, tile: TileView? ->

            if (tile != null) {

                for (direction in Direction.values()) {

                    val vector = getVector(direction) ?: continue
                    val cell = Coordinates(coordinates.x + vector.x, coordinates.y + vector.y)

                    val other = grid.cellContent(cell)

                    if (other !== null && other.value == tile.value) {
                        result = true // These two tiles can be merged
                    }
                }
            }
        }

        return result
    }

}