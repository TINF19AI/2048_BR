package com.dhbw.br2048.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.Direction
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var b: ActivityGameBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        setCurrentFragment(gridFragment)

        b.btMove.setOnClickListener {
        }

        b.btReset.setOnClickListener {
        }

        b.btAppear.setOnClickListener {
        }

        b.btMerge.setOnClickListener {
        }
    }

    override fun onResume() {
        super.onResume()
        manager = GameManager(b.root.context, gridFragment.getGrid(), Coordinates(4,4), 1)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return when(keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                manager.move(Direction.UP)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                manager.move(Direction.LEFT)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                manager.move(Direction.DOWN)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                manager.move(Direction.RIGHT)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}