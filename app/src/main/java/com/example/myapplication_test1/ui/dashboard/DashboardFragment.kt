package com.example.myapplication_test1.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 이미지 목록 (삐삐와 팬텀 이미지)
        val images = listOf(
            R.drawable.pipi,
            R.drawable.phantom,
            R.drawable.pikachu,
            R.drawable.chikorita,
            R.drawable.jamanbo,
            R.drawable.kobugi
        )

        // RecyclerView 설정
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2열 그리드
        recyclerView.adapter = GalleryAdapter(images)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inner Class로 GalleryAdapter 구현
    inner class GalleryAdapter(private val imageList: List<Int>) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

        inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: android.widget.ImageView = itemView.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery, parent, false)
            return GalleryViewHolder(view)
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            holder.imageView.setImageResource(imageList[position])
        }

        override fun getItemCount(): Int = imageList.size
    }
}
