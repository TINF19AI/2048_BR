package com.dhbw.br2048.data

import android.content.Context
import android.widget.GridLayout
import com.dhbw.br2048.presentation.TileView

class GameManager(
    private val context: Context,
    private val gridLayout: GridLayout,
    private val gridSize: Coordinates,
    private val startTiles: Int,
) {
    private val grid = Grid(gridSize)

    var wonCallback: ((Boolean) -> Any?)? = null
    private var won = false
        set(newValue) {
            wonCallback?.let { it(won) }
            field = newValue
        }

    var overCallback: ((Boolean) -> Any?)? = null
    private var over = false
        set(newValue) {
            overCallback?.let { it(won) }
            field = newValue
        }

    var scoreCallback: ((Int) -> Any?)? = null
    private var score = 0
        set(newValue) {
            scoreCallback?.let { it(score) }
            field = newValue
        }

    init {
        addStartTiles()
    }

    // Set up the initial tiles to start the game with
    private fun addStartTiles() {
        for (i in 1..startTiles) {
            this.addRandomTile()
        }
    }

    // Adds a tile in a random position
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

    // Move a tile and its representation
    private fun moveTile(tile: TileView, cell: Coordinates) {
        grid.move(tile, cell)
    }

    // Get the vector representing the chosen direction
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
    fun move(direction: Direction) {
        // 0: up, 1: right, 2: down, 3: left

        //if (this.isGameTerminated()) return; // Don't do anything if the game's over

        var cell: Coordinates
        var tile: TileView?

        val vector = getVector(direction) ?: return
        val traversals = buildTraversals(vector)
        var moved = false

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
                        next.value = tile.value * 2
                        next.coordinates = positions.second
                        next.mergedFrom = arrayOf(tile, next)

                        next.merge()

                        // Replace old tile
                        grid.removeTile(tile)
                        tile.removeFromGrid()

                        moved = true

                        // Update the score
                        score += next.value

                        // The mighty 2048 tile
                        if (next.value == 2048) won = true
                    } else {
                        moveTile(tile, positions.first)
                    }

                    if (!cell.isEqual(tile.coordinates)) {
                        moved = true // The tile moved from its original cell!
                    }
                }
            }
        }

        if (moved) {
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

    private fun movesAvailable(): Boolean {
        return grid.cellsAvailable() || tileMatchesAvailable()
    }

    // Check for available matches between tiles (more expensive check)
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