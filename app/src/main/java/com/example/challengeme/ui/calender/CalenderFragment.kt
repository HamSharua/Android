package com.example.challengeme.ui.calender

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.challengeme.databinding.FragmentCalenderBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!
    private lateinit var calenderViewModel: CalenderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calenderViewModel = ViewModelProvider(this).get(CalenderViewModel::class.java)
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: CalendarView = binding.calendarView
        val editTextNote: EditText = binding.editTextNote
        val buttonSaveNote: Button = binding.buttonSaveNote

        // SharedPreferencesから保存されたメモを取得
        val sharedPref = requireActivity().getSharedPreferences("calenderNotes", Context.MODE_PRIVATE)

        // カレンダーの日付変更リスナーを設定
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // 日付が変更されたときの処理
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.JAPAN)
            val date = dateFormat.format(calendar.time)

            // 選択された日付に対応するメモを表示
            val note = sharedPref.getString(date, "")
            editTextNote.setText(note)
        }

        // ボタンのクリックリスナーを設定
        buttonSaveNote.setOnClickListener {
            val note = editTextNote.text.toString()
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.JAPAN).format(calendarView.date)
            with(sharedPref.edit()) {
                putString(selectedDate, note)
                apply()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
