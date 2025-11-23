package me.yuuto.mynd.ui.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.yuuto.mynd.R

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalendarFragment())
                .commit()
        }
    }
}
