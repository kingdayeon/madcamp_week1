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
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import java.io.File
import java.io.IOException
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val CAMERA_REQUEST_CODE = 101

    // 이미지 목록 (기본 이미지 + 동적으로 추가될 이미지)
    private val PREFS_NAME = "ImagePrefs"
    private val IMAGES_KEY = "imageList"
    private lateinit var sharedPreferences: SharedPreferences
    private val imageList = mutableListOf<Any>(
        R.drawable.baseimg01,
        R.drawable.baseimg02,
        R.drawable.baseimg03,
        R.drawable.baseimg04,
        R.drawable.baseimg05
    )
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val photoUri = data?.data ?: data?.extras?.get("data") as? Bitmap
            if (photoUri is Bitmap) {
                val uri = saveBitmapToFile(photoUri)
                if (uri != null) {
                    addImage(uri) // 촬영된 사진 추가
                }
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val externalDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(externalDir, "captured_image_${System.currentTimeMillis()}.jpg")
        return try {
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val externalDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(externalDir, "gallery_image_${System.currentTimeMillis()}.jpg")

        return try {
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // json 데이터타입
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        val json = sharedPreferences.getString(IMAGES_KEY, null)
        Log.d("DEBUG", "Initial SharedPreferences JSON: $json")
        loadImages()

        // RecyclerView 설정
        galleryAdapter = GalleryAdapter(imageList)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2열 그리드
        binding.recyclerView.adapter = galleryAdapter

        // "이미지 추가" 버튼 클릭 이벤트
        binding.addImageButton.setOnClickListener {
            val options = arrayOf("갤러리에서 선택", "카메라로 촬영")
            AlertDialog.Builder(requireContext())
                .setTitle("이미지 추가")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> { // 갤러리에서 선택
                            (activity as? com.example.myapplication_test1.MainActivity)?.galleryLauncher?.launch("image/*")
                        }
                        1 -> { // 카메라로 촬영
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                        }
                        else -> {} // 기타 경우
                    }
                }
                .show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        saveImages() // 뷰가 파괴될 때 저장
        Log.d("DEBUG", "ImageList after load: ${imageList.joinToString()}")
    }

    // 선택된 이미지를 추가하는 메서드
    fun addImage(uri: Uri) {
        // 갤러리에서 선택한 URI를 내부 저장소로 복사
        val savedUri = copyUriToInternalStorage(uri)
        if (savedUri != null) {
            imageList.add(0, savedUri) // 저장된 URI를 리스트에 추가
            galleryAdapter.notifyDataSetChanged()
            saveImages() // 변경된 목록 저장
        } else {
            Toast.makeText(requireContext(), "이미지를 저장하지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveImages() {
        val editor = sharedPreferences.edit()
        val paths = imageList.map { item ->
            when (item) {
                is Int -> item.toString() // 리소스 ID는 그대로 저장
                is Uri -> item.path       // 파일 경로 저장
                else -> null
            }
        }.filterNotNull() // null 제거
        val json = Gson().toJson(paths)
        editor.putString(IMAGES_KEY, json)
        editor.apply()
        Log.d("DEBUG", "SharedPreferences JSON after save: $json")
    }

    private fun loadImages() {
        val json = sharedPreferences.getString(IMAGES_KEY, null)
        if (json != null) {
            Log.d("DEBUG", "SharedPreferences JSON before load: $json")
            val type = object : TypeToken<MutableList<String>>() {}.type
            val savedPaths: MutableList<String> = Gson().fromJson(json, type)
            imageList.clear()
            savedPaths.forEach { path ->
                if (path.toIntOrNull() != null) {
                    imageList.add(path.toInt()) // 리소스 ID 복원
                } else {
                    val file = File(path)
                    if (file.exists()) {
                        imageList.add(Uri.fromFile(file)) // 파일 경로를 URI로 변환하여 복원
                    }
                }
            }
            Log.d("DEBUG", "ImageList after load: ${imageList.joinToString()}")
        }else {
            Log.d("DEBUG", "SharedPreferences is empty during loadImages")
        }
    }

    // RecyclerView Adapter
    inner class GalleryAdapter(private val images: MutableList<Any>) :
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
            if (position >= images.size) {
                // 빈 뷰 처리
                holder.imageView.setImageDrawable(null) // 빈 뷰에 이미지를 비워둠
            } else {
                val item = images[position]
                if (item is Int) {
                    holder.imageView.setImageResource(item) // 리소스 ID로 이미지 설정
                } else if (item is Uri) {
                    holder.imageView.setImageURI(item) // URI로 이미지 설정
                }

                // 이미지 삭제 처리
                holder.itemView.setOnLongClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("이미지 삭제")
                        .setMessage("이미지를 삭제하시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            images.removeAt(position)
                            notifyDataSetChanged()
                            saveImages()
                            Toast.makeText(requireContext(), "이미지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("아니오", null)
                        .show()
                    true
                }
            }
        }

        override fun getItemCount(): Int {
            return images.size // 데이터 개수만 반환
        }
    }


}
