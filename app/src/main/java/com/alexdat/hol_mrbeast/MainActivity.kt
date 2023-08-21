package com.alexdat.hol_mrbeast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.main_button_start -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
            R.id.main_button_highscores -> {
                val intent = Intent(this, HighscoresActivity::class.java)
                startActivity(intent)
            }
            R.id.main_button_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.main_button_exit -> {
                finish()
            }
        }
    }
}
