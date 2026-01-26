package me.yuuto.mynd.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.yuuto.mynd.NoteActivity
import me.yuuto.mynd.R
import me.yuuto.mynd.databinding.CalendarBinding
import me.yuuto.mynd.notes.NoteDatabase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: CalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedDate: LocalDate
    private lateinit var notesAdapter: CalendarNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = LocalDate.now()
        setupMonthView()
        setupNotesView()
        setupClickListeners()
        updateSelectedDateUI() // Initial load
    }

    private fun setupMonthView() {
        binding.monthLabel.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth) { day ->
            if (day.isNotBlank()) {
                selectedDate = selectedDate.withDayOfMonth(day.toInt())
                updateSelectedDateUI()
            }
        }
        binding.calendarRecycler.layoutManager = GridLayoutManager(context, 7)
        binding.calendarRecycler.adapter = calendarAdapter
    }

    private fun setupNotesView() {
        notesAdapter = CalendarNotesAdapter(emptyList()) { note ->
            val intent = Intent(requireContext(), NoteActivity::class.java)
            intent.putExtra("note_id", note.id)
            startActivity(intent)
        }
        binding.notesRecycler.layoutManager = LinearLayoutManager(context)
        binding.notesRecycler.adapter = notesAdapter
    }

    private fun daysInMonthArray(date: LocalDate): List<String> {
        val daysInMonthArray = mutableListOf<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = date.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1 until dayOfWeek) {
            daysInMonthArray.add("")
        }
        for (i in 1..daysInMonth) {
            daysInMonthArray.add(i.toString())
        }
        while (daysInMonthArray.size % 7 != 0) {
            daysInMonthArray.add("")
        }
        return daysInMonthArray
    }

    private fun setupClickListeners() {
        binding.btnPrev.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setupMonthView()
        }
        binding.btnNext.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setupMonthView()
        }
        binding.todayPill.setOnClickListener {
            selectedDate = LocalDate.now()
            setupMonthView()
        }
        binding.btnAddEntry.setOnClickListener {
            val intent = Intent(requireContext(), NoteActivity::class.java)
            intent.putExtra("selected_date", selectedDate.toString())
            startActivity(intent)
        }
    }

    private fun updateSelectedDateUI() {
        val formatter = DateTimeFormatter.ofPattern("E, dd. MMM yyyy", Locale.GERMAN)
        binding.selectedDate.text = selectedDate.format(formatter)

        CoroutineScope(Dispatchers.IO).launch {
            val notesForDate = NoteDatabase.getDatabase(requireContext()).noteDao().getNotesByDate(selectedDate.toString())
            withContext(Dispatchers.Main) {
                notesAdapter.updateNotes(notesForDate)
                binding.entryCount.text = resources.getQuantityString(R.plurals.entry_count, notesForDate.size, notesForDate.size)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateSelectedDateUI() // Refresh notes when returning to the fragment
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN)
        return date.format(formatter).replaceFirstChar { it.titlecase(Locale.GERMAN) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
