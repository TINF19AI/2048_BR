package com.dhbw.br2048.data

import com.dhbw.br2048.presentation.TileView
import kotlin.math.floor

class Grid(
    private val size: Coordinates,
) {

    private var grid: Array<Array<TileView?>> = Array(size.x) { Array(size.y) { null } }

    // From open source 2048, ported by Maxi
    fun randomAvailableCell(): Coordinates? {
        val cells = availableCells()

        if (cells.size > 0) {
            return cells[floor(Math.random() * cells.size).toInt()]
        }

        return null
    }

    // From open source 2048, ported by Maxi
    fun move(tile:TileView, newPosition: Coordinates){
        grid[tile.coordinates.x][tile.coordinates.y] = null
        grid[newPosition.x][newPosition.y] = tile
        tile.coordinates = newPosition
    }

    // From open source 2048, ported by Maxi
    private fun availableCells(): MutableList<Coordinates> {
        val cells: MutableList<Coordinates> = mutableListOf()

        eachCell { coordinates, tile ->
            if (tile == null) {
                cells.add(coordinates)
            }
        }

        return cells
    }

    // From open source 2048, ported by Maxi
    fun eachCell(callback: (Coordinates, TileView?) -> Any) {
        for ((x, column) in grid.withIndex()) {
            for ((y, tile) in column.withIndex()) {
                callback(Coordinates(x, y), tile)
            }
        }
    }

    // From open source 2048, ported by Maxi
    fun withinBounds(position: Coordinates): Boolean {
        return position.x >= 0 && position.x < grid.size &&
                position.y >= 0 && position.y < grid.size
    }

    // From open source 2048, ported by Maxi
    fun insertTile(tile: TileView) {
        grid[tile.coordinates.x][tile.coordinates.y] = tile
    }

    // From open source 2048, ported by Maxi
    fun removeTile(tile: TileView) {
        grid[tile.coordinates.x][tile.coordinates.y] = null
    }

    // Check if there are any cells available
    // From open source 2048, ported by Maxi
    fun cellsAvailable(): Boolean {
        return this.availableCells().size > 0
    }

    // Check if the specified cell is taken
    // From open source 2048, ported by Maxi
    fun cellAvailable(cell: Coordinates): Boolean {
        return this.cellContent(cell) == null
    }

    // From open source 2048, ported by Maxi
    fun cellOccupied(cell: Coordinates): Boolean {
        return !cellAvailable(cell)
    }

    // From open source 2048, ported by Maxi
    fun cellContent(cell: Coordinates): TileView? {
        return if (withinBounds(cell)) {
            grid[cell.x][cell.y]
        } else {
            null
        }
    }

    fun clearGrid() {
        for ((x, column) in grid.withIndex()) {
            for ((y, _) in column.withIndex()) {
                val tile = grid[x][y]
                if (tile != null) {
                    tile.removeFromGrid()
                    removeTile(tile)
                }
            }
        }
    }
}