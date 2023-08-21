package com.alexdat.hol_mrbeast

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources


class HighscoresActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat", "DiscouragedApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)

        val table_view = findViewById<TableLayout>(R.id.tablelayout)
        val highscore_list = get_highscores_from_file(filesDir)

        for (place in 1..highscore_list.size) {
            val row_view = TableRow(this)
            val views = listOf(TextView(this), TextView(this), TextView(this))
            views[1].text = highscore_list[place - 1].first.toString()
            views[2].text =
                SimpleDateFormat("dd MMMM yyyy, HH:mm:ss").format(highscore_list[place - 1].second * 1000L)
            for (i in 0 until 3) {
                views[i].gravity = Gravity.CENTER_HORIZONTAL
                views[i].textSize = 15F
                views[i].layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                row_view.addView(views[i])
            }

            if (place <= 3) {
                views[0].text = "$place"
                val id = resources.getIdentifier(
                    "place_$place",
                    "drawable",
                    applicationContext.packageName
                )
                AppCompatResources.getDrawable(applicationContext, id)
                views[0].setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(
                        applicationContext,
                        id
                    ), null, null, null
                )
            } else {
                views[0].text = "     $place"
            }
            row_view.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            table_view.addView(row_view)
        }
    }
}
