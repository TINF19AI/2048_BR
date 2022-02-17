package com.dhbw.br2048.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme from shared preferences
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        setTheme(sp.getInt("currentTheme", R.style.Theme_Original))

        super.onCreate(savedInstanceState)

        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setCurrentFragment(gridFragment)

        b.btChangeTheme.setOnClickListener {
            // https://stackoverflow.com/questions/13832459/android-how-to-refresh-activity-set-theme-dynamically
            val sp = getSharedPreferences("theme", MODE_PRIVATE)
            val spe = sp.edit()
            spe.putInt("currentTheme", R.style.Theme_Pink) // TODO: theme selection
            spe.apply()
            Log.d("SettingsActivity", "Theme was changed")

            val intent = this.intent
            this.finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            this.startActivity(intent)
        }

        b.btShowLicenses.setOnClickListener {
            MaterialAlertDialogBuilder(
                b.root.context,
                com.google.android.material.R.style.MaterialAlertDialog_Material3_Body_Text_CenterStacked
            ).setMessage("test")
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            2,
        )
        manager.setTile(2, Coordinates(0, 0))
        manager.setTile(4, Coordinates(1, 0))
        manager.setTile(8, Coordinates(2, 0))
        manager.setTile(16, Coordinates(3, 0))
        manager.setTile(32, Coordinates(0, 1))
        manager.setTile(64, Coordinates(1, 1))
        manager.setTile(128, Coordinates(2, 1))
        manager.setTile(256, Coordinates(3, 1))
        manager.setTile(512, Coordinates(0, 2))
        manager.setTile(1024, Coordinates(1, 2))
        manager.setTile(2048, Coordinates(2, 2))
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}