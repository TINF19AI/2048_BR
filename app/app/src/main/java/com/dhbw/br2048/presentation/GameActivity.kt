package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.R
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.*
import com.dhbw.br2048.databinding.ActivityGameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


class GameActivity : BaseActivity() {
    private lateinit var b: ActivityGameBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager
    private var gameSocket: GameSocket? = null
    private lateinit var timer: CountDownTimer

    private var scoreList: MutableList<Score> = mutableListOf()
    private lateinit var scoreAdapter: ScoreAdapter

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

        b.rvScores.layoutManager = LinearLayoutManager(b.root.context, RecyclerView.VERTICAL, false)
        scoreAdapter = ScoreAdapter(scoreList)
        b.rvScores.adapter = scoreAdapter

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

        b.btReturn.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d("GameActivity", "back button pressed")
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (manager.over) {
            super.onBackPressed()
            return
        }

        MaterialAlertDialogBuilder(
            b.root.context,
            R.style.AlertDialogRegular
        ).setMessage(getString(R.string.game_exit_confirmation))
            .setNegativeButton(getString(R.string.continue_playing)) { _, _ ->
                // do nothing when canceled
            }.setPositiveButton(getString(R.string.exit)) { _, _ ->
                super.onBackPressed()
            }
            .show()
    }


    // Author: Maxi
    override fun onResume() {
        super.onResume()
        var noOneAlive = false
        val extras = intent.extras
        val gameId = extras?.getString(Constants.BUNDLE_KEY_GAMEID)
        if (gameId != null && gameId != "") {
            b.cardScoreboard.visibility = View.VISIBLE // show scoreboard in multiplayer

            gameSocket = GameSocket(
                gameId,
                getUserId()
            ) { list, score -> // callback function when new score

                runOnUiThread {
                    if (!score.alive) {
                        manager.alive = false
                        showEndScreen()
                        displayPoints(score.score, gameId)
                        b.tvEndPosition.text = getString(R.string.position, score.position)
                        b.tvEndMessage.text = getString(R.string.points_num, score.score)

                        if (score.position == 1) {
                            b.tvEndHeader.text = getString(R.string.game_won_quote)
                        } else {
                            b.tvEndHeader.text = getString(R.string.game_over)
                        }
                    }

                    b.tvPosition.text =
                        getString(R.string.position_current_total, score.position, list.size)

                    scoreList.clear()
                    var alive = false
                    for (s in list) {
                        scoreList.add(s)
                        if (s.alive) alive = true
                    }
                    if (!alive && this::timer.isInitialized) {
                        timer.cancel()
                    }
                    scoreAdapter.notifyDataSetChanged()
                }
            }

            gameSocket!!.socket.on(Constants.SOCK_LOBBYDETAILS) { lobbyJson ->
                val lobby = (lobbyJson[0] as JSONObject).toLobby()
                Log.d("lobbyDetails", lobby.toString())

                if (lobby.duration != -1) {
                    runOnUiThread {
                        timer = object : CountDownTimer((lobby.duration).toLong(), 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                b.tvTime.text = getString(
                                    R.string.time_num,
                                    ((millisUntilFinished + 999) / 1000)
                                )
                            }

                            override fun onFinish() {
                                b.tvTime.text = getString(R.string.time_0)
                            }
                        }.start()
                    }
                }
            }

            gameSocket!!.socket.emit(Constants.SOCK_LOBBYDETAILS, null)
            gameSocket!!.socket.emit(Constants.SOCK_SCORE, 0)

            gameSocket!!.socket.on(Constants.SOCK_DISCONNECT) {
                checkConnection(0)
            }

        }
        else { // Singleplayer
            b.cardScoreboard.visibility = View.GONE
        }

        if(!this::manager.isInitialized){
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
                Snackbar.make(b.tvScore, getString(R.string.game_over), Snackbar.LENGTH_LONG).show()
                setEndScore()
                showEndScreen()
                gameSocket?.over(score)
                setHighscore(score)
                displayPoints(score, gameId)
            }
            manager.wonCallback = { score: Int ->
                Snackbar.make(b.tvScore, getString(R.string.game_won), Snackbar.LENGTH_LONG).show()
                setEndScore()
                gameSocket?.won(score)
                setHighscore(score)
                displayPoints(score, gameId)
            }
        }


    }

    private fun setEndScore() {

    }

    private fun displayPoints(score: Int, gameId: String?) {
        var pointsString = getString(R.string.points_num, score)
        if (gameId == "")
            pointsString += "\n" + getString(R.string.highscore_num, getHighscore())
        b.tvScore.text = pointsString
    }

    private fun checkConnection(attempt: Int) {
        if (gameSocket?.isConnected() == false && manager.alive) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (attempt == 2) {
                    runOnUiThread {
                        Snackbar.make(
                            b.tvScore,
                            getString(R.string.connection_lost),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                if (attempt == 9) {
                    runOnUiThread {
                        Snackbar.make(
                            b.tvScore,
                            getString(R.string.reconnect_failed),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                if (attempt > 9) {
                    runOnUiThread {
                        Snackbar.make(
                            b.tvScore,
                            getString(R.string.reconnect_failed),
                            Snackbar.LENGTH_LONG
                        ).show()
                        gameSocket!!.close()
                        startActivity(Intent(this, GameSelectionActivity::class.java))
                    }

                } else {
                    checkConnection(attempt + 1)
                }
            }, 2000)
        } else {
            if (attempt >= 2) {
                runOnUiThread {
                    Snackbar.make(b.tvScore, R.string.reconnect_successful, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
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
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        return sp.getInt(Constants.SP_KEY_HIGHSCORE, 0)
    }

    private fun setHighscore(score: Int) {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val highscore = sp.getInt(Constants.SP_KEY_HIGHSCORE, 0)

        if (score > highscore) {
            val spe = sp.edit()
            spe.putInt(Constants.SP_KEY_HIGHSCORE, score)
            spe.apply()
        }
    }
}