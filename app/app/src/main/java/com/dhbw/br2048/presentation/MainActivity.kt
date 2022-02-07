package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var tile: TileView

    private val gridFragment = GridFragment()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setCurrentFragment(gridFragment)

        tile = TileView(b.flFragment.context, null, Coordinates(1, 2), 8)

        b.btMove.setOnClickListener {
            if (Random.nextInt(0, 2) == 0) {
                // X
                tile.coordinates = Coordinates(Random.nextInt(0, 4), tile.coordinates.y)
            } else {
                // Y
                tile.coordinates = Coordinates(tile.coordinates.x, Random.nextInt(0, 4))
            }
        }

        b.btReset.setOnClickListener {
            tile.removeFromGrid()
        }

        b.btAppear.setOnClickListener {
        }

        b.btMerge.setOnClickListener {
        }

        b.flFragment.setOnTouchListener(object: OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                tile.coordinates = Coordinates(0, tile.coordinates.y)
            }
            override fun onSwipeRight() {
                tile.coordinates = Coordinates(3, tile.coordinates.y)
            }
            override fun onSwipeTop() {
                tile.coordinates = Coordinates(tile.coordinates.x, 0)
            }
            override fun onSwipeBottom() {
                tile.coordinates = Coordinates(tile.coordinates.x, 3)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        tile.setGridLayout(gridFragment.getGrid())
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}