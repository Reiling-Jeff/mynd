package me.yuuto.mynd.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.yuuto.mynd.databinding.CalendarDayBinding

class CalendarAdapter(
    private val daysOfMonth: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(private val binding: CalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            binding.dayOfMonth.text = day
            itemView.setOnClickListener {
                if (day.isNotBlank()) {
                    onItemClick(day)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(daysOfMonth[position])
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }
}
