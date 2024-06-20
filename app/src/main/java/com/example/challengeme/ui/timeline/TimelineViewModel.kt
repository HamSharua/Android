package com.example.challengeme.ui.timeline

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.example.challengeme.data.ChallengeContract
//import com.example.challengeme.data.ChallengeDbHelper
//import java.text.SimpleDateFormat
//import java.util.*
class TimelineViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}
