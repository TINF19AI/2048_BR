package com.dhbw.br2048.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setMargins
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates

class TileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, coord: Coordinates, startValue: Int
) : AppCompatTextView(context, attrs) {


    var coordinates: Coordinates = coord
        set(newCoordinates) {
            updatePosition(newCoordinates)
            field = newCoordinates
        }

    //@todo Initialize with constructor
    var value: Int = startValue
        set(newValue) {
            this.text = newValue.toString()
            field = newValue
        }

    var grid: GridLayout? = null

    init {
        this.value = startValue
    }

    fun setGridLayout(newGrid: GridLayout) {
        grid = newGrid

        // only set these parameters if tile is visible in grid
        this.setBackgroundResource(R.drawable.shape_tile)
        this.textAlignment = TEXT_ALIGNMENT_GRAVITY
//        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.gravity = Gravity.CENTER
        this.textSize = context.dpToPx(10).toFloat()

        this.layoutParams = setCoordLayoutParams(this.coordinates)

        grid!!.addView(this)
    }

    fun removeFromGrid() {
        grid?.removeView(this)
    }

    fun updatePosition(newCoord: Coordinates) {
        grid?.let {
            // is executed when run != null
            animateTo(newCoord)
        }
    }

    private fun animateTo(newCoord: Coordinates) {
        val fieldDistanceX = newCoord.x.minus(this.coordinates.x)
        val fieldDistanceY = newCoord.y.minus(this.coordinates.y)

        // @todo Prevent bugs if function is run multiple times during animation duration

        this.animate()
            .translationX(fieldDistanceX * (this.width.toFloat() + grid!!.paddingRight))
            .translationY(fieldDistanceY * (this.height.toFloat() + grid!!.paddingBottom))
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                moveTo(newCoord)
            }
            .start()
    }

    private fun moveTo(newCoord: Coordinates) {
        this.apply {
            val params = (this.layoutParams as GridLayout.LayoutParams)
            this.layoutParams = setCoordLayoutParams(newCoord)

            translationX = 0f
            translationY = 0f
        }
    }

    fun setCoordLayoutParams(
        coord: Coordinates
    ): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams(
            GridLayout.spec(coord.y, GridLayout.FILL), // rowSpec
            GridLayout.spec(coord.x, GridLayout.FILL) // columnSpec
        )
        params.apply {
            width = 0
            height = 0
            setMargins(10)
        }

        return params
    }

    fun merge() {
        this.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                this.animate()
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }

    fun appear() {
        this.scaleX = 0f
        this.scaleY = 0f

        this.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(250)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }
}

//https://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.pxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).toInt()
}
