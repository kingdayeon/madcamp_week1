
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.util.TypeMapper
import java.util.Calendar

class FriendAdapter(
    private val friendList: List<Friend>,
    private val onFriendDelete: (Friend) -> Unit  // 친구 삭제 콜백 추가
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }



    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]

        // 이름 설정
        holder.nameTextView.text = friend.name

        // 학교와 나이 설정
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val age = currentYear - (friend.birthYear.toIntOrNull() ?: currentYear)
        holder.schoolAndAgeTextView.text = "${friend.school} • ${age}살"

        // TypeMapper를 통해 색상 정보 가져오기
        val pokemonType = TypeMapper.getTypeByMbti(friend.mbti)

        // 뱃지 색상 및 텍스트 설정
        holder.badgeTextView.text = pokemonType.name
        holder.badgeTextView.background.setColorFilter(
            android.graphics.Color.parseColor(pokemonType.color),
            android.graphics.PorterDuff.Mode.SRC_IN
        )


        // 배경 원 색상 설정 (투명도 포함)
        val backgroundColor = Color.parseColor(pokemonType.backgroundColor)
        val gradientDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(backgroundColor)
        }
        holder.imageBackground.background = gradientDrawable

        // 프로필 이미지 설정
        holder.pokemonImageView.setImageResource(friend.imageResourceId)

        // 아이템 클릭 시 상세 다이얼로그 표시
        holder.itemView.setOnClickListener {
            showFriendDetailDialog(it.context, friend)
        }
    }

    override fun getItemCount(): Int = friendList.size

    // ViewHolder 정의
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        val schoolAndAgeTextView: TextView = itemView.findViewById(R.id.text_school_age)
        val badgeTextView: TextView = itemView.findViewById(R.id.text_badge)
        val pokemonImageView: ImageView = itemView.findViewById(R.id.image_profile)
        val imageBackground: View = itemView.findViewById(R.id.image_background)
    }

    // 각 타입별 이미지 리스트 정의
    private val groundImages = listOf(R.drawable.ground_1, R.drawable.ground_2)    // ISTJ
    private val rockImages = listOf(R.drawable.rock_1, R.drawable.rock_2)          // ISFJ
    private val phychicImages = listOf(R.drawable.phychic_1, R.drawable.phychic_2) // INFJ
    private val darkImages = listOf(R.drawable.dark_1, R.drawable.dark_2)          // INTJ
    private val normalImages = listOf(R.drawable.normal_1, R.drawable.normal_2)    // ISTP
    private val fairyImages = listOf(R.drawable.fairy_1, R.drawable.fairy_2)       // ISFP
    private val waterImages = listOf(R.drawable.water_1, R.drawable.water_2)       // INFP
    private val ghostImages = listOf(R.drawable.ghost_1, R.drawable.ghost_2)       // INTP
    private val dragonImages = listOf(R.drawable.dragon_1, R.drawable.dragon_2)    // ESTP
    private val electricImages = listOf(R.drawable.electric_1, R.drawable.electric_2) // ESFP
    private val flyingImages = listOf(R.drawable.flying_1, R.drawable.flying_2)    // ENFP
    private val fightingImages = listOf(R.drawable.fighting_1, R.drawable.fighting_2) // ENTP
    private val steelImages = listOf(R.drawable.steel_1, R.drawable.steel_2)       // ESTJ
    private val grassImages = listOf(R.drawable.grass_1, R.drawable.grass_2)       // ESFJ
    private val fireImages = listOf(R.drawable.fire_1, R.drawable.fire_2)          // ENFJ
    private val poisonImages = listOf(R.drawable.poison_1, R.drawable.poison_2)    // ENTJ

    // 속성에 따라 랜덤하게 이미지 선택
    private fun getDrawableResIdByType(type: String): Int {
        return when (type) {
            "땅" -> groundImages.random()
            "바위" -> rockImages.random()
            "에스퍼" -> phychicImages.random()
            "악" -> darkImages.random()
            "노말" -> normalImages.random()
            "페어리" -> fairyImages.random()
            "물" -> waterImages.random()
            "고스트" -> ghostImages.random()
            "드래곤" -> dragonImages.random()
            "전기" -> electricImages.random()
            "비행" -> flyingImages.random()
            "격투" -> fightingImages.random()
            "강철" -> steelImages.random()
            "풀" -> grassImages.random()
            "불꽃" -> fireImages.random()
            "독" -> poisonImages.random()
            else -> R.drawable.placeholder_image
        }
    }
    private fun showCustomToast(context: Context, message: String, isError: Boolean) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toastText)
        textView.text = message

        // 텍스트 색상 설정 (빨간색으로 고정)
        textView.setTextColor(Color.RED)

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
    private fun showFriendDetailDialog(context: Context, friend: Friend) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_friend_detail)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)

        // 뷰 참조
        val nameMonText = dialog.findViewById<TextView>(R.id.nameMonText)
        val levelText = dialog.findViewById<TextView>(R.id.levelText)
        val pokemonImage = dialog.findViewById<ImageView>(R.id.pokemonImage)
        val imageBackground = dialog.findViewById<View>(R.id.imageBackground)
        val typeBadge = dialog.findViewById<TextView>(R.id.typeBadge)
        val schoolBadge = dialog.findViewById<TextView>(R.id.schoolBadge)
        val phoneBadge = dialog.findViewById<TextView>(R.id.phoneBadge)
        val deleteButton = dialog.findViewById<TextView>(R.id.deleteButton)

        // 포켓몬 타입 정보 가져오기
        val pokemonType = TypeMapper.getTypeByMbti(friend.mbti)

        // 이름과 레벨 설정
        nameMonText.text = "${friend.name}몬"
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = currentYear - (friend.birthYear.toIntOrNull() ?: currentYear)
        levelText.text = "Lv.${age}"

//        // 이미지와 배경 설정
//        pokemonImage.setImageResource(friend.imageResourceId)
//        imageBackground.background.setColorFilter(
//            Color.parseColor(pokemonType.backgroundColor),
//            PorterDuff.Mode.SRC_IN
//        )

        // 이미지와 배경 설정
        pokemonImage.setImageResource(friend.imageResourceId)
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.getDimension(R.dimen.corner_radius)
            setColor(Color.parseColor(pokemonType.backgroundColor))
        }
        imageBackground.background = bgDrawable

        // 속성 뱃지 설정
        typeBadge.text = pokemonType.name
        typeBadge.background.setColorFilter(
            Color.parseColor(pokemonType.color),
            PorterDuff.Mode.SRC_IN
        )

        // 정보 뱃지 설정
        schoolBadge.text = friend.school
        phoneBadge.text = friend.phoneNumber

        // 전화번호 클릭 시 전화 앱으로 연결
        phoneBadge.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${friend.phoneNumber}")
            }
            context.startActivity(intent)
        }



        // 삭제 버튼 클릭 시
        deleteButton.setOnClickListener {
            onFriendDelete(friend)
            showCustomToast(context, "${friend.name}몬이(가) 방출되었습니다!", true)
            dialog.dismiss()
        }

        dialog.show()
    }
}


