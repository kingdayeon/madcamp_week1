package com.example.myapplication_test1.ui.dashboard

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication_test1.R

class GalleryAdapter(private val imageList: List<Any>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_gallery, parent, false) // 기존 레이아웃 사용
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = imageList[position]
        if (item is Int) {
            // 정적 리소스 이미지
            holder.imageView.setImageResource(item)
        } else if (item is Uri) {
            // 동적 URI 이미지
            holder.imageView.load(item) // Coil 라이브러리로 이미지 로드
        }
    }

    override fun getItemCount(): Int = imageList.size
}
