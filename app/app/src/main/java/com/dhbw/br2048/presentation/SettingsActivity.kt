package com.dhbw.br2048.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.R
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

        b.btChangeTheme.setOnClickListener {
            // https://stackoverflow.com/questions/13832459/android-how-to-refresh-activity-set-theme-dynamically
            val sp = getSharedPreferences("theme", MODE_PRIVATE)
            val spe = sp.edit()
            spe.putInt("currentTheme", R.style.Theme_Pink) // TODO: theme selection
            spe.apply()
            Log.d("SettingsActivity", "Theme was changed")
            this.recreate()
        }

        b.btShowLicenses.setOnClickListener {
            MaterialAlertDialogBuilder(
                b.root.context,
                com.google.android.material.R.style.MaterialAlertDialog_Material3_Body_Text_CenterStacked
            ).setMessage("test")
                .show()
        }

    }
}