package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import com.dhbw.br2048.R
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.Direction
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityGameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


class GameActivity : BaseActivity() {
    private lateinit var b: ActivityGameBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager
    private var gameSocket: GameSocket? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, gridFragment)
            commit()
        }


        setToolbar(b.abTop)
        b.cardEndScreen.alpha = 0f

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
            R.style.AlertDialogRegular
        ).setMessage(resources.getString(R.string.game_exit_confirmation))
            .setNegativeButton(resources.getString(R.string.continue_playing)) { _, _ ->
                // do nothing when canceled
            }.setPositiveButton(resources.getString(R.string.exit)) { _, _ ->
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
        val gameId = extras?.getString("gameID")
            if (gameId != null && gameId != "") {
                gameSocket = GameSocket(
                    gameId,
                    getUserId()
                ) { list, score ->

                    runOnUiThread {
                        if(!score.alive){
                            manager.alive = false
                            showEndScreen()
                            "${score.position}.".also { b.tvEndPosition.text = it }
                            displayPoints(score.score, gameId)
                            "Points: ${score.score}".also { b.tvEndMessage.text = it }
                            b.tvEndHeader.text = if (score.position == 1) "Winner winner chicken dinner " else "Game Over!"
                        }

                        "${score.position} / ${list.size}".also { b.tvPosition.text = it }


                        for ((i, entry) in list.withIndex()) {
                            val pos = i + 1
                            if (pos in 1..3) {
                                scoreboardScores[i].text = entry.score.toString()
                                scoreboardUsernames[i].text = entry.username
                            }
                        }
                    }
                }

                gameSocket!!.socket.on("lobbyDetails"){ lobbyJson ->
                    val lobby = (lobbyJson[0] as JSONObject).toLobby()
                    Log.d("lobbyDetails", lobby.toString())

                    if(lobby.duration != -1){
                        runOnUiThread {
                            object : CountDownTimer((lobby.duration).toLong(), 1000) {

                                override fun onTick(millisUntilFinished: Long) {
                                    "${((millisUntilFinished + 999) / 1000)}s".also { b.tvTime.text = it }
                                }

                                override fun onFinish() {
                                    "0s".also { b.tvTime.text = it }
                                }
                            }.start()

                        }
                    }
                }

                gameSocket!!.socket.emit("lobbyDetails", null)
                gameSocket!!.socket.emit("score", 0)

        }
        displayPoints(0, gameId)


        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            2,
        )
        manager.addStartTiles()

        manager.scoreCallback = { score: Int ->
            displayPoints(score, gameId)
            gameSocket?.score(score)
        }
        manager.overCallback = { score: Int ->
            Snackbar.make(b.tvScore, "Game Over!", Snackbar.LENGTH_LONG).show()
            "Points: $score".also { b.tvEndMessage.text = it }
            showEndScreen()
            gameSocket?.over(score)
            setHighscore(score)
            displayPoints(score, gameId)
        }
        manager.wonCallback = { score: Int ->
            Snackbar.make(b.tvScore, "Wow good Job... Nerd!", Snackbar.LENGTH_LONG).show()
            "Points: $score".also { b.tvEndMessage.text = it }
            gameSocket?.won(score)
            setHighscore(score)
            displayPoints(score, gameId)
        }
    }

    private fun displayPoints(score: Int, gameId: String?) {
        var pointsString = "Points: $score"
        if (gameId == "")
            pointsString += "\nHighscore: ${getHighscore()}"
        pointsString.also { b.tvScore.text = it }
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

    private fun showEndScreen() {
        Log.d("GameActivity", "showEndScreen")
        b.cardEndScreen.animate()
            .alpha(0.8f)
            .setDuration(1000)
            .start()
    }

    private fun hideEndScreen() {
        b.cardEndScreen.animate()
            .alpha(0f)
            .setDuration(1000)
            .start()
    }

    private fun getHighscore(): Int {
        val sp = getSharedPreferences("general", MODE_PRIVATE)
        return sp.getInt("highscore", 0)
    }

    private fun setHighscore(score: Int) {
        val sp = getSharedPreferences("general", MODE_PRIVATE)
        val highscore = sp.getInt("highscore", 0)

        if (score > highscore) {
            val spe = sp.edit()
            spe.putInt("highscore", score)
            spe.apply()
        }
    }
}