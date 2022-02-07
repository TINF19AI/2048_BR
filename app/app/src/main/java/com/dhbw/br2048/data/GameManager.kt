package com.dhbw.br2048.data

import android.content.Context
import com.dhbw.br2048.presentation.TileView

class GameManager (
        val context: Context,
        val gridSize: Coordinates,
        val startTiles: Int
    ){
    var won = false
    var over = false
    val grid = Grid(gridSize)
    var score = 0

    // Set up the initial tiles to start the game with
    fun addStartTiles () {
        for (i in 1..startTiles) {
            this.addRandomTile()
        }
    }

    // Adds a tile in a random position
    fun addRandomTile() {
        if (grid.cellsAvailable()) {
            val value = if(Math.random() < 0.9) 2 else 4
            val tile = grid.randomAvailableCell()?.let { TileView(context, pos = it, startValue = value) }
            if (tile != null) {
                grid.insertTile(tile)
            }
        }
    }

    // Move a tile and its representation
    fun moveTile(tile: TileView, cell:Coordinates) {
        grid.move(tile, cell)
    }

    // Get the vector representing the chosen direction
    fun getVector (direction: Direction): Coordinates? {
        // Vectors representing tile movement
        val map = mapOf(
            0 to Coordinates( 0, -1 ),
            1 to Coordinates( 1, 0 ),
            2 to Coordinates( 0, 1 ),
            3 to Coordinates( -1, 0 )
        )

        return map.get(direction.direction)
    }

    // Build a list of positions to traverse in the right order
    fun buildTraversals (vector: Coordinates): Pair<MutableList<Int>, MutableList<Int>> {
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
        var tile: TileView

        val vector     = getVector(direction)
        if(vector == null) return
        val traversals = buildTraversals(vector)
        var moved      = false

        // Traverse the grid in the right direction and move tiles
        for (x in traversals.first){
            for (y in traversals.second){

                cell = Coordinates( x, y )
                tile = grid.cellContent(cell)!!

                //if (tile != null) {
                    val positions = findFarthestPosition(cell, vector)
                    val next      = grid.cellContent(positions.second)

                    // Only one merger per row traversal?
                    if (next != null && next.value == tile.value /*&& !next.mergedFrom*/) {
                        //var merged = new Tile(positions.next, tile.value * 2);
                        //merged.mergedFrom = [tile, next];

                        //grid.move(tile)
                        //grid.insertTile(merged);
                        //grid.removeTile(tile);

                        // Converge the two tiles' positions
                        //tile.updatePosition(positions.next);

                        // Update the score
                        //score += merged.value;

                        // The mighty 2048 tile
                        //if (merged.value === 2048) won = true;
                    } else {
                        moveTile(tile, positions.first)
                    }

                    if (!cell.isEqual(tile.coordinates)) {
                        moved = true // The tile moved from its original cell!
                    }
                //}
            }
        }

        if (moved) {
            this.addRandomTile()

            if (!movesAvailable()) {
                over = true // Game over!
            }

            //actuate();
        }
    }

    fun findFarthestPosition (cell: Coordinates, vector: Coordinates): Pair<Coordinates, Coordinates> {
        var previous: Coordinates
        var newCell = cell

        // Progress towards the vector direction until an obstacle is found
        do {
            previous = newCell
            newCell     = Coordinates(previous.x + vector.x, previous.y + vector.y )
        } while (this.grid.withinBounds(newCell) &&
            this.grid.cellAvailable(newCell))

        return Pair(previous, cell)// Used to check if a merge is required

    }

    fun movesAvailable (): Boolean {
        return grid.cellsAvailable() || tileMatchesAvailable()
    }

    // Check for available matches between tiles (more expensive check)
    fun tileMatchesAvailable ():Boolean {
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