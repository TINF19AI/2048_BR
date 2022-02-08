package com.dhbw.br2048.presentation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.setMargins
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates

class TileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, pos: Coordinates, startValue: Int
) : AppCompatTextView(context, attrs) {

    var mergedFrom: Array<TileView>? = null

    var coordinates: Coordinates = pos
    var deleteOnMove = false
    var updateOnMerge = true

    //@todo Initialize with constructor
    var value: Int = startValue
        set(newValue) {
            if (!updateOnMerge) {
                updateText()
            }
            field = newValue
        }

    var grid: GridLayout? = null

    init {
        this.value = startValue
        updateText()
    }

    fun updateText() {
        this.text = value.toString()
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

        appear()
    }

    fun removeFromGrid() {
        grid?.removeView(this)
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

    fun mergeAnimation(removedTile: TileView): Animator {
        val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1.2f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1.2f, 1.0f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleX, scaleY)
        animator.doOnStart {
            updateText()
        }
        animator.doOnEnd {
            grid?.removeView(removedTile)
        }

        return animator
    }

    fun moveToAnimation(newCoord: Coordinates): Animator {
        val fieldDistanceX = newCoord.x.minus(this.coordinates.x)
        val fieldDistanceY = newCoord.y.minus(this.coordinates.y)

        val deltaX = PropertyValuesHolder.ofFloat(
            TRANSLATION_X,
            fieldDistanceX * (this.width.toFloat() + grid!!.paddingRight)
        )

        val deltaY = PropertyValuesHolder.ofFloat(
            TRANSLATION_Y,
            fieldDistanceY * (this.height.toFloat() + grid!!.paddingBottom)
        )
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, deltaX, deltaY)
        animator.doOnEnd {
            if (deleteOnMove) {
                removeFromGrid()
            } else {
                moveTo(newCoord) // update grid position
            }
        }
        // @todo Prevent bugs if function is run multiple times during animation duration

        return animator
    }

    private fun moveTo(newCoord: Coordinates) {
        this.apply {
            //val params = (this.layoutParams as GridLayout.LayoutParams)
            this.layoutParams = setCoordLayoutParams(newCoord)

            translationX = 0f
            translationY = 0f
        }
    }

    fun appear() {
        this.scaleX = 0f
        this.scaleY = 0f

        this.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setStartDelay(250)
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
