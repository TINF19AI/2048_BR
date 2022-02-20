package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.Direction
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityGameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.socket.emitter.Emitter


class GameActivity : AppCompatActivity() {
    private lateinit var b: ActivityGameBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager
    private var gameSocket: GameSocket? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme from shared preferences
        // Author: Kai
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        setTheme(sp.getInt("currentTheme", R.style.Theme_Original))
        // End Kai

        super.onCreate(savedInstanceState)

        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        setCurrentFragment(gridFragment)

        // Author: Caspar
        b.clGame.setOnTouchListener(object : OnSwipeTouchListener(this@GameActivity) {
            override fun onSwipeLeft() {
                manager.move(Direction.LEFT)
            }

            override fun onSwipeRight() {
                manager.move(Direction.RIGHT)
            }

            override fun onSwipeTop() {
                manager.move(Direction.UP)
            }

            override fun onSwipeBottom() {
                manager.move(Direction.DOWN)
            }
        })
        // End Caspar
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(
            b.root.context,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        ).setMessage(resources.getString(R.string.game_exit_confirmation))
            .setNegativeButton(resources.getString(R.string.continue_playing)) { dialog, which ->
                // do nothing when canceled
            }.setPositiveButton(resources.getString(R.string.exit)) { dialog, which ->
                super.onBackPressed()
            }
            .show()
    }


    // Author: Maxi
    override fun onResume() {
        super.onResume()

        val scoreboardUsernames = arrayOf(
            b.scoreboard1Username,
            b.scoreboard2Username,
            b.scoreboard3Username
        )
        val scoreboardScores = arrayOf(
            b.scoreboard1Score,
            b.scoreboard2Score,
            b.scoreboard3Score
        )

        val extras = intent.extras
        var gameId = extras?.getString("gameID")
        gameId?.let {
            if (it != "") {
                gameSocket = GameSocket(
                    gameId,
                    Settings.Global.getString(baseContext.contentResolver, "device_name")
                ) { list, position ->

                    runOnUiThread {
                        b.position.text = "$position / ${list.size}"

                        for ((i, entry) in list.withIndex()) {
                            val pos = i + 1
                            if (pos in 1..3) {
                                scoreboardScores[i].text = entry.score.toString()
                                scoreboardUsernames[i].text = entry.username

                            }
                        }
                    }
                }
            }
        }

        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            2,
        )
        manager.addStartTiles()

        manager.scoreCallback = { score: Int ->
            b.score.text = score.toString()
            gameSocket?.score(score)
        }
        manager.overCallback = { score: Int ->
            Snackbar.make(b.score, "Game Over!", Snackbar.LENGTH_LONG).show()
            gameSocket?.over(score)

        }
        manager.wonCallback = { score: Int ->
            Snackbar.make(b.score, "Wow good Job... Nerd!", Snackbar.LENGTH_LONG).show()
            gameSocket?.won(score)

        }
    }

    override fun onStop() {
        gameSocket?.close()
        Log.d("GameActivity", "onStop")
        super.onStop()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
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