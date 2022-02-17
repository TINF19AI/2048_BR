package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private var gridFragment = GridFragment()
    private lateinit var manager: GameManager
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        val spe = sp.edit()
        spe.putInt("currentTheme", R.style.Theme_Original)
        spe.apply()
        Log.d("GameActivity", "reset to default theme")

        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        SocketHandler.setSocket(b.root.context)

        b.btStartGame.setOnClickListener {
            startActivity(Intent(this, GameSelection::class.java))
        }

        b.btSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, gridFragment)
            commit()
        }
    }

    private fun newBackgroundGame(){

        gridFragment.clearGrid()

        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            2,
        )
        manager.addStartTiles()
        // @todo reset after time or score
        /* manager.scoreCallback = {
            if (it > 100){
                newBackgroundGame()
            }
        }*/

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    manager.moveRandom()
                }
            }
        }, 1000, 2500)
    }

    override fun onResume() {
        super.onResume()
        newBackgroundGame()

    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }
}