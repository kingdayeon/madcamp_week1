package com.example.myapplication_test1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.data.Appointment
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentAdapter(
    private val appointmentList: MutableList<Appointment>, //그냥 리스트와 뭔 차이?
    private val onLocationClick: (LatLng) -> Unit,
    private val onDeleteClick: (Int) -> Unit  // 삭제 콜백 추가
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    private lateinit var appointmentAdapter: AppointmentAdapter

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deleteButton: TextView = itemView.findViewById(R.id.deleteButton)

        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val friendText: TextView = itemView.findViewById(R.id.friendText)
        val locationText: TextView = itemView.findViewById(R.id.locationText)
        val memoText: TextView = itemView.findViewById(R.id.memoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]

        // 날짜 표시
        holder.dateText.text = dateFormat.format(Date(appointment.date))

        // 제목 표시
        holder.titleText.text = appointment.title

        // 삭제 버튼 클릭 리스너
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)  // Fragment에서 처리하도록 콜백 전달
        }

        // 친구 표시 ("친구와 약속" 또는 "XX 친구와 약속")
        holder.friendText.visibility = View.VISIBLE
        if (appointment.friends.isNotEmpty()) {
            val friend = appointment.friends[0]  // 첫 번째(유일한) 친구
            holder.friendText.text = "${friend.name} 친구와 약속 🍀"
        } else {
            holder.friendText.text = "친구와 약속 🍀"
        }

        // 위치 표시 로직 수정
        holder.locationText.visibility = View.VISIBLE  // 항상 보이게 설정
        if (appointment.locationLatLng != null && appointment.location.isNotEmpty()
            && appointment.location != "포켓스톱") {
            holder.locationText.text = "📍 ${appointment.location}"
            holder.locationText.setOnClickListener {
                onLocationClick(appointment.locationLatLng)
            }
        } else {
            holder.locationText.text = "\uD83D\uDCCD 포켓스톱"
            holder.locationText.setOnClickListener(null)  // 클릭 리스너 제거
        }


        // 메모 표시 (있는 경우에만)
        if (appointment.memo.isNotEmpty()) {
            holder.memoText.visibility = View.VISIBLE
            holder.memoText.text = appointment.memo
        } else {
            holder.memoText.visibility = View.GONE
        }
    }

    override fun getItemCount() = appointmentList.size

    // 날짜순 정렬을 위한 함수 추가
    fun getSortedList(): List<Appointment> {
        return appointmentList.sortedBy { it.date }
    }
}