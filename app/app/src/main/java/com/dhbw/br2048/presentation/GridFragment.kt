package com.dhbw.br2048.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Interpolator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.dhbw.br2048.R
import com.dhbw.br2048.databinding.FragmentGridBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [grid.newInstance] factory method to
 * create an instance of this fragment.
 */
class GridFragment : Fragment() {
    private var _binding: FragmentGridBinding? = null
    private val b get() = _binding!!


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var columns: Int = 4
    private var rows: Int = 4

    private var tile: TileView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGridBinding.inflate(layoutInflater, container, false)
        return b.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // settings for grid
        b.grid2048.apply {
            this.rowCount = 4
            this.columnCount = 4
            this.setBackgroundColor(resources.getColor(R.color.md_brown_300, null))
            this.setPadding(20)
        }

        for (i in 0 until 16) {
            val empty = TextView(b.grid2048.rootView.context)
            empty.setBackgroundResource(R.drawable.shape_tile_empty)

            b.grid2048.addView(empty)
            empty.layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            )
            (empty.layoutParams as GridLayout.LayoutParams).setMargins(20)
        }

        tile = TileView(b.grid2048.rootView.context)
        tile!!.text = "2"
        b.grid2048.addView(tile)
        tile!!.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(1, GridLayout.FILL),
            GridLayout.spec(1, GridLayout.FILL)
        )
        (tile!!.layoutParams as GridLayout.LayoutParams).setMargins(20)
    }

    fun move() {
        // TODO: remove hardcoded translationY value
        val animator = ObjectAnimator.ofFloat(tile, "translationY", 350f).apply {
            duration = 200

            doOnEnd {
                tile!!.apply {
                    val params = (this.layoutParams as GridLayout.LayoutParams)
                    params.rowSpec = GridLayout.spec(2, GridLayout.FILL)
                    this.layoutParams = params
                    translationY = 0f
                }

                Log.d("GridFragment", "onEnd called")
            }

        }
        animator.start()
    }

    fun reset() {
        tile!!.apply {
            val params = (this.layoutParams as GridLayout.LayoutParams)
            params.rowSpec = GridLayout.spec(1, GridLayout.FILL)
            this.layoutParams = params
        }
    }

    fun merge() {
        tile!!.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                tile!!.animate()
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }

    fun appear() {
        tile!!.scaleX = 0f
        tile!!.scaleY = 0f

        tile!!.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(250)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }
}