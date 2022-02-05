package com.dhbw.br2048.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.dhbw.br2048.R


class TileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var x : Int = -1
    var y : Int = -1
    var value : Int = -1


    init {
        Log.d("TileView", "TileView created")
        this.setBackgroundResource(R.drawable.shape_tile)
        this.textAlignment = TEXT_ALIGNMENT_GRAVITY
//        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.gravity = Gravity.CENTER
        this.textSize = context.dpToPx(10).toFloat()

    }

}

//https://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.pxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).toInt()
}
