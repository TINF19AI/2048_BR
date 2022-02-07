package com.dhbw.br2048.data

import com.dhbw.br2048.presentation.TileView

class Grid(
    private val size: Coordinates,
) {

    private var grid: Array<Array<TileView?>> = Array(size.x) { Array(size.y) { null } }


    // @todo import
    // @todo export

    fun randomAvailableCell(): Coordinates? {
        var cells = availableCells()

        if (cells.size > 0) {
            return cells[Math.floor(Math.random() * cells.size).toInt()]
        }

        return null
    }

    fun move(tile:TileView, newPosition: Coordinates){
        grid[tile.coordinates.x][tile.coordinates.y] = null
        grid[newPosition.x][newPosition.y] = tile
        tile.coordinates = newPosition
    }

    private fun availableCells(): MutableList<Coordinates> {
        var cells: MutableList<Coordinates> = mutableListOf()

        eachCell { coordinates, tile ->
            if (tile == null) {
                cells.add(coordinates)
            }
        }

        return cells
    }

    fun eachCell(callback: (Coordinates, TileView?) -> Any) {
        for ((x, column) in grid.withIndex()) {
            for ((y, tile) in column.withIndex()) {
                callback(Coordinates(x, y), tile)
            }
        }
    }

    fun withinBounds(position: Coordinates): Boolean {
        return position.x >= 0 && position.x < grid.size &&
                position.y >= 0 && position.y < grid.size
    }


    fun insertTile(tile: TileView) {
        grid[tile.coordinates.x][tile.coordinates.y] = tile
    }

    fun removeTile(tile: TileView) {
        grid[tile.coordinates.x][tile.coordinates.y] = null
    }

    // Check if there are any cells available
    fun cellsAvailable(): Boolean {
        return this.availableCells().size > 0
    }

    // Check if the specified cell is taken
    fun cellAvailable(cell: Coordinates): Boolean {
        return this.cellContent(cell) == null
    }

    fun cellOccupied(cell: Coordinates): Boolean {
        return !cellAvailable(cell)
    }

    fun cellContent(cell: Coordinates): TileView? {
        return if (withinBounds(cell)) {
            grid[cell.x][cell.y]
        } else {
            null
        }
    }
}