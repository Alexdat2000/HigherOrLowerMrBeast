package com.alexdat.hol_mrbeast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView


class GameEndActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_end)
        val score = intent.getIntExtra("score", 0)
        val highscore = intent.getIntExtra("highscore", 0)
        val left_title = intent.getStringExtra("left_title")
        val left_viewcount = intent.getIntExtra("left_viewcount", 0)
        val left_filename = intent.getStringExtra("left_filename")
        val right_title = intent.getStringExtra("right_title")
        val right_viewcount = intent.getIntExtra("right_viewcount", 0)
        val right_filename = intent.getStringExtra("right_filename")

        val video1 = findViewById<TextView>(R.id.video_1)
        val video2 = findViewById<TextView>(R.id.video_2)
        val score_view = findViewById<TextView>(R.id.score)

        showOption(video1, VideoInfo(left_title!!, left_filename!!, left_viewcount), applicationContext, true)
        showOption(video2, VideoInfo(right_title!!, right_filename!!, right_viewcount), applicationContext, true)
        score_view.text = getString(R.string.score_with_high_template, score, highscore)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.retry_button -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.exit_button -> {
                finishAndRemoveTask()
            }
        }
    }
}
