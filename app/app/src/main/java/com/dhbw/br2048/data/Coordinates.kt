package com.dhbw.br2048.data

class Coordinates(var x: Int,var y: Int) {

    fun isEqual(second: Coordinates): Boolean {
        return x == second.x && y == second.y
    }
}