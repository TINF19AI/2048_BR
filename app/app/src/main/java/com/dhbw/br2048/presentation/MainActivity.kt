package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityMainBinding
import java.util.*

class MainActivity : BaseActivity() {

    private lateinit var b: ActivityMainBinding
    private var gridFragment = GridFragment()
    private var manager: GameManager? = null
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        Log.d("MainActivity", "game started")
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        generateUserIdIfEmpty()

        SocketHandler.setSocket(getUsername(), getUserId())

        b.btStartGame.setOnClickListener {
            startActivity(Intent(this, GameSelectionActivity::class.java))
        }

        b.btSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, gridFragment)
            commit()
        }

    }

    private fun generateUserIdIfEmpty() {
        val sp = getSharedPreferences("general", MODE_PRIVATE)
        val userId = sp.getString("userId", "")
        Log.d("MainActivity", "UserID: $userId")
        if (userId == "") {
            val spe = sp.edit()
            val newGeneratedUserId = UUID.randomUUID().toString()
            spe.putString("userId", newGeneratedUserId) // TODO: theme selection
            spe.apply()
            Log.d("MainActivity", "Generated new UserID: $newGeneratedUserId")
        }

    }

    private fun newBackgroundGame(){
        manager?.clearGrid()

        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            4,
        )
        manager?.addStartTiles()
        // @todo reset after time or score
        /* manager.scoreCallback = {
            if (it > 100){
                newBackgroundGame()
            }
        }*/
        manager?.overCallback = {
            runOnUiThread {
                manager?.clearGrid()
                manager?.addStartTiles()
            }
        }

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    manager?.moveRandom()
                }
            }
        }, 1000, 2000)
    }

    override fun onResume() {
        super.onResume()
        newBackgroundGame()
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}