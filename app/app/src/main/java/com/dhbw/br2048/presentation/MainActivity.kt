package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.Constants
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random

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

        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        // if username is empty
        if (sp.getString(Constants.SP_KEY_USERNAME, "") == "")
            setRandomUsername()
    }

    private fun generateUserIdIfEmpty() {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val userId = sp.getString(Constants.SP_KEY_USERID, "")
        Log.d("MainActivity", "UserID: $userId")
        if (userId == "") {
            val spe = sp.edit()
            val newGeneratedUserId = UUID.randomUUID().toString()
            spe.putString(Constants.SP_KEY_USERID, newGeneratedUserId) // TODO: theme selection
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

        manager?.overCallback = {
            runOnUiThread {
                manager?.clearGrid()
                manager?.addStartTiles()
            }
        }

        manager?.wonCallback = {
            timer?.cancel()
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

    private fun setRandomUsername() {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val spe = sp.edit()
        spe.putString(Constants.SP_KEY_USERNAME, "Slider ${Random.nextInt(10000, 100000)}")
        spe.apply()
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