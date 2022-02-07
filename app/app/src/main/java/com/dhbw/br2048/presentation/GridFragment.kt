package com.dhbw.br2048.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.databinding.FragmentGridBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GridFragment : Fragment() {
    private var _binding: FragmentGridBinding? = null
    private val b get() = _binding!!

    private var columns: Int = 4
    private var rows: Int = 4

    private var tile: TileView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGridBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    fun getGrid(): GridLayout {
        return b.grid2048
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

        for (i in 0 until 4) {
            for (k in 0 until 4) {
                val empty = TextView(b.grid2048.rootView.context)
                empty.setBackgroundResource(R.drawable.shape_tile_empty)

                // weight = 1: every row/column has equal height/width
                empty.layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(i, GridLayout.FILL, 1f),
                    GridLayout.spec(k, GridLayout.FILL, 1f)
                )
                // set height and width to 0 to prevent using background drawable height/width
                // -> causes visual bugs
                empty.layoutParams.height = 0
                empty.layoutParams.width = 0
                b.grid2048.addView(empty)
                (empty.layoutParams as GridLayout.LayoutParams).setMargins(20)
            }

        }

    }
}