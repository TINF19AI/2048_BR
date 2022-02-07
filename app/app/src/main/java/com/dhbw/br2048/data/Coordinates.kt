package com.dhbw.br2048.data

import android.content.Context

class Coordinates(var x: Int,var y: Int) {

    fun isEqual(second: Coordinates): Boolean {
        return x === second.x && y === second.y;
    }
}