package com.dhbw.br2048.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.Constants
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class SettingsActivity : BaseActivity() {
    private lateinit var b: ActivitySettingsBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager

    private val themeText = arrayOf(
        "Default",
        "Ocean",
        "Fire",
        "Retro",
        "Unicorn",
        "Pale Rainbow",
        "All Black",
        "Metallic"
    )

    private val themeId = arrayOf(
        R.style.Theme_Original,
        R.style.Theme_Ocean,
        R.style.Theme_Fire,
        R.style.Theme_Retro,
        R.style.Theme_Unicorn,
        R.style.Theme_PaleRainbow,
        R.style.Theme_AllBlack,
        R.style.Theme_Metallic
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setCurrentFragment(gridFragment)
        setToolbar(b.abTop)

        b.btChangeTheme.setOnClickListener {
            MaterialAlertDialogBuilder(
                b.root.context,
                R.style.AlertDialogRegular
            )
                .setNeutralButton(getString(R.string.cancel)) { _, _ ->

                }
                .setSingleChoiceItems(
                    themeText,
                    themeId.indexOf(this.currentThemeId)
                ) { dialog, which ->
                    // https://stackoverflow.com/questions/13832459/android-how-to-refresh-activity-set-theme-dynamically
                    val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
                    val spe = sp.edit()
                    val newTheme = themeId[which]
                    spe.putInt(Constants.SP_KEY_THEME, newTheme)
                    spe.apply()
                    this.currentThemeId = newTheme
                    Log.d("SettingsActivity", "Theme was changed")
                    dialog.dismiss()
                    runOnUiThread {
                        val intent = this.intent
                        this.finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        this.startActivity(intent)
                    }
                }
                .show()
        }

        b.textInputUsername.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveUsername()
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                return@OnEditorActionListener true
            }
            false
        })

        b.btChangeUsername.setOnClickListener {
            saveUsername()
        }

        b.btShowLicenses.setOnClickListener {
            val url = "https://github.com/Tanikai/2048_BR/blob/main/LICENSE-NOTICES.md"
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
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

        b.textInputUsername.setText(getUsername())
    }

    private fun saveUsername() {
        var username = b.textInputUsername.text.toString()
        username = username.trim()

        if (isUsernameValid(username)) {
            val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
            val spe = sp.edit()
            spe.putString(Constants.SP_KEY_USERNAME, username)
            spe.apply()
            SocketHandler.setSocket(username, getUserId())
            b.textInputUsername.setText(username)
            Snackbar.make(
                b.btChangeUsername,
                getString(R.string.username_saved),
                Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        val length = username.length

        if (length > 16) {
            Snackbar.make(
                b.btChangeUsername,
                getString(R.string.longer_than_20_chars),
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }
        if (length < 3) {
            Snackbar.make(
                b.btChangeUsername,
                getString(R.string.shorter_than_3),
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        finish()
        super.onBackPressed()
    }

}
