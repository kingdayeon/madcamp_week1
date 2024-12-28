package com.example.myapplication_test1.ui.dashboard

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // 이미지 목록 (기본 이미지 + 동적으로 추가될 이미지)
    private val imageList = mutableListOf<Any>( // Int 또는 Uri를 저장하기 위해 Any 사용
        R.drawable.pipi,
        R.drawable.phantom,
        R.drawable.pikachu,
        R.drawable.chikorita,
        R.drawable.jamanbo,
        R.drawable.kobugi
    )
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView 설정
        galleryAdapter = GalleryAdapter(imageList)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2열 그리드
        binding.recyclerView.adapter = galleryAdapter

        // "이미지 추가" 버튼 클릭 이벤트
        binding.addImageButton.setOnClickListener {
            (activity as? com.example.myapplication_test1.MainActivity)?.galleryLauncher?.launch("image/*")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 선택된 이미지를 추가하는 메서드
    fun addImage(uri: Uri) {
        imageList.add(uri) // 선택된 이미지를 리스트에 추가
        galleryAdapter.notifyDataSetChanged() // RecyclerView 업데이트
    }

    // RecyclerView Adapter
    inner class GalleryAdapter(private val images: List<Any>) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

        inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: android.widget.ImageView = itemView.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery, parent, false) // 단일 이미지 레이아웃
            return GalleryViewHolder(view)
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val item = images[position]
            if (item is Int) { // 리소스 ID
                holder.imageView.setImageResource(item)
            } else if (item is Uri) { // URI
                holder.imageView.setImageURI(item)
            }
        }

        override fun getItemCount(): Int = images.size
    }
}
