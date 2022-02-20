package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        Log.d("MainActivity", "game started")
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        SocketHandler.setSocket(b.root.context)

        b.btStartGame.setOnClickListener {
            startActivity(Intent(this, GameSelectionActivity::class.java))
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

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    manager?.moveRandom()
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

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}