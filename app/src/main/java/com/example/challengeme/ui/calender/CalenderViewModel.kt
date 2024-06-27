package com.example.challengeme.ui.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalenderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Select a date"
    }
    val text: LiveData<String> = _text

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    // 日付ごとのメモを保存するためのマップ
    private val notes = mutableMapOf<String, String>()

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun saveNoteForDate(date: String, note: String) {
        notes[date] = note
    }

    fun getNoteForDate(date: String): String {
        return notes[date] ?: ""
    }
}
