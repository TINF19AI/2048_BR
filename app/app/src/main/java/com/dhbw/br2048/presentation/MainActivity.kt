package com.dhbw.br2048.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    private val gridFragment = GridFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setCurrentFragment(gridFragment)

        b.btMove.setOnClickListener {
            gridFragment.move()
        }

        b.btReset.setOnClickListener {
            gridFragment.reset()
        }

        b.btAppear.setOnClickListener {
            gridFragment.appear()
        }

        b.btMerge.setOnClickListener {
            gridFragment.merge()

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