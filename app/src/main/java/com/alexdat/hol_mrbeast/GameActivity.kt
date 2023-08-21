package com.alexdat.hol_mrbeast

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlin.random.Random


const val WIDTH = 16
const val HEIGHT = 9
const val SCALE = 40


@SuppressLint("DiscouragedApi")
fun getResizedThumbnail(context: Context, filename: String): Drawable {
    Log.d("IMAGE", filename)
    val resourceId = context.resources.getIdentifier(filename, "drawable", context.packageName)
    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val bitmap_scaled = Bitmap.createScaledBitmap(bitmap, WIDTH * SCALE, HEIGHT * SCALE, true)
    return BitmapDrawable(context.resources, bitmap_scaled)
}


class VideoInfo(title_: String, filename_: String, viewcount_: Int) {
    val filename = filename_
    val title = title_
    val viewcount = viewcount_
}


fun showOption(
    textview: TextView?, videoinfo: VideoInfo, applicationContext: Context, show_viewcount: Boolean
) {
    textview?.setCompoundDrawablesWithIntrinsicBounds(
        null, getResizedThumbnail(applicationContext, videoinfo.filename), null, null
    )
    textview?.text = if (!show_viewcount) {
        "${videoinfo.title}\n??? million views"
    } else {
        "${videoinfo.title}\n${videoinfo.viewcount} million views"
    }
}


class GameState(
    left_text_view_: TextView?,
    right_text_view_: TextView?,
    score_text_view_: TextView?,
    app_context_: Context
) {
    var left_ind: Int = 0
    var right_ind: Int = 0
    var score: Int = 0
    val left_text_view = left_text_view_
    val right_text_view = right_text_view_
    val score_text_view = score_text_view_
    val app_context = app_context_
    var videos = mutableListOf<VideoInfo>()

    fun generate_index(len: Int): Int {
        return Random.nextInt(0, len)
    }

    fun generate_index(len: Int, ban: Int): Int {
        var gen = Random.nextInt(0, len - 1)
        if (gen >= ban) {
            gen += 1
        }
        return gen
    }

    init {
        videos = getVideosFromFile()
        Log.d("vid", videos.size.toString())
        left_ind = generate_index(videos.size)
        right_ind = generate_index(videos.size, left_ind)
        showOption(left_text_view, videos[left_ind], app_context, true)
        showOption(right_text_view, videos[right_ind], app_context, false)
    }

    private fun getVideosFromFile(): MutableList<VideoInfo> {
        val x = mutableListOf<VideoInfo>()
        val data_string = app_context.assets.open("data.txt").bufferedReader().use {
            it.readText()
        }
        data_string.lines().forEach {
            x.add(VideoInfo(it.split(";")[0], it.split(";")[1], it.split(";")[2].toInt()))
        }
        return x
    }

    fun correctAnswer() {
        MediaPlayer.create(app_context, R.raw.correct).start()
        score += 1
        score_text_view?.text = app_context.getString(R.string.score_template, score)
        left_ind = right_ind
        right_ind = generate_index(videos.size, left_ind)
        showOption(left_text_view, videos[left_ind], app_context, true)
        showOption(right_text_view, videos[right_ind], app_context, false)
    }

    fun isHigherCorrect(): Boolean {
        return videos[left_ind].viewcount <= videos[right_ind].viewcount
    }

    fun isLowerCorrect(): Boolean {
        return videos[left_ind].viewcount >= videos[right_ind].viewcount
    }
}

fun get_highscores_from_file(filesDir: File): MutableList<Pair<Int, Int>> {
    val highscore_list = mutableListOf<Pair<Int, Int>>()
    val highscores_file = File(filesDir, "highscores.csv")
    if (highscores_file.exists()) {
        val text = highscores_file.bufferedReader().use {
            it.readText().trim()
        }
        Log.d("FILE", text)
        text.lines().forEach {
            highscore_list.add(Pair(it.split(";")[0].toInt(), it.split(";")[1].toInt()))
        }
    }
    return highscore_list
}


class GameActivity : AppCompatActivity(), View.OnClickListener {
    private var game_state: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val video1 = findViewById<TextView>(R.id.video_1)
        val video2 = findViewById<TextView>(R.id.video_2)
        val score = findViewById<TextView>(R.id.score)
        game_state = GameState(video1, video2, score, applicationContext)
    }

    fun game_end(score: Int) {
        val highscore_list = get_highscores_from_file(filesDir)
        highscore_list.add(Pair(score, (System.currentTimeMillis() / 1000).toInt()))
        highscore_list.sortBy { -it.first }
        if (highscore_list.size > 10) {
            highscore_list.removeAt(10)
        }
        var new_file_data = ""
        for (p in highscore_list) {
            new_file_data += "${p.first};${p.second}\n"
        }
        applicationContext.openFileOutput("highscores.csv", Context.MODE_PRIVATE).use {
            it.write(new_file_data.toByteArray())
        }

        val intent = Intent(this, GameEndActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("highscore", highscore_list[0].first)
        intent.putExtra("left_title",
            game_state?.left_ind?.let { game_state?.videos?.get(it)?.title })
        intent.putExtra("left_viewcount",
            game_state?.left_ind?.let { game_state?.videos?.get(it)?.viewcount })
        intent.putExtra("left_filename",
            game_state?.left_ind?.let { game_state?.videos?.get(it)?.filename })
        intent.putExtra("right_title",
            game_state?.right_ind?.let { game_state?.videos?.get(it)?.title })
        intent.putExtra("right_viewcount",
            game_state?.right_ind?.let { game_state?.videos?.get(it)?.viewcount })
        intent.putExtra("right_filename",
            game_state?.right_ind?.let { game_state?.videos?.get(it)?.filename })
        startActivity(intent)
        finish()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.higher_button -> {
                if (game_state!!.isHigherCorrect()) {
                    game_state?.correctAnswer()
                } else {
                    MediaPlayer.create(applicationContext, R.raw.wrong).start()
                    game_end(game_state!!.score)
                }
            }

            R.id.lower_button -> {
                if (game_state!!.isLowerCorrect()) {
                    game_state?.correctAnswer()
                } else {
                    MediaPlayer.create(applicationContext, R.raw.wrong).start()
                    game_end(game_state!!.score)
                }
            }
        }
    }
}
