//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import coil.load
//import com.example.myapplication_test1.R
//import com.example.myapplication_test1.data.Friend
//
//class FriendAdapter(private val friendList: List<Friend>) :
//    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
//        return FriendViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
//        val friend = friendList[position]
//
//        // 이름 설정
//        holder.nameTextView.text = friend.name
//
//        // 학교와 나이 설정
//        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
//        val age = currentYear - (friend.birthYear.toIntOrNull() ?: currentYear)
//        holder.schoolAndAgeTextView.text = "${friend.school} • ${age}살"
//
//        // 뱃지 색상 및 텍스트 설정
//        holder.badgeTextView.text = friend.type
//        holder.badgeTextView.setBackgroundColor(android.graphics.Color.parseColor(friend.backgroundColor))
//
//        // 이미지 로드 (Coil 사용)
//        holder.pokemonImageView.load(getPokemonImageUrl(friend.type)) {
//            placeholder(R.drawable.placeholder_image) // 로딩 중 표시할 이미지
//            error(R.drawable.error_image) // 로드 실패 시 표시할 이미지
//            crossfade(true) // 크로스페이드 애니메이션
//        }
//    }
//
//    override fun getItemCount(): Int = friendList.size
//
//    // ViewHolder 정의
//    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val nameTextView: TextView = itemView.findViewById(R.id.text_name)
//        val schoolAndAgeTextView: TextView = itemView.findViewById(R.id.text_school_age)
//        val badgeTextView: TextView = itemView.findViewById(R.id.text_badge)
//        val pokemonImageView: ImageView = itemView.findViewById(R.id.image_profile)
//    }
//
//    // 속성에 따라 포켓몬 이미지 URL 반환
//    private fun getPokemonImageUrl(type: String): String {
//        return when (type) {
//            "물" -> "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/000701.png"
//            "노말" -> "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/001601.png"
//            "독" -> "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/002301.png"
//            "페어리" -> "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/003501.png"
//            else -> "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/default.png" // 기본 이미지
//        }
//    }
//}
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.util.TypeMapper

class FriendAdapter(private val friendList: List<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

//    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
//        val friend = friendList[position]
//
//        // 이름 설정
//        holder.nameTextView.text = friend.name
//
//        // 학교와 나이 설정
//        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
//        val age = currentYear - (friend.birthYear.toIntOrNull() ?: currentYear)
//        holder.schoolAndAgeTextView.text = "${friend.school} • ${age}살"
//
//        // 뱃지 색상 및 텍스트 설정
//        holder.badgeTextView.text = friend.type
//        holder.badgeTextView.setBackgroundColor(android.graphics.Color.parseColor(friend.backgroundColor))
//
//        // 이미지 로드 (드로워블에서 가져오기)
//        holder.pokemonImageView.setImageResource(getDrawableResIdByType(friend.type))
//    }

//    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
//        val friend = friendList[position]
//
//        // 이름 설정
//        holder.nameTextView.text = friend.name
//
//        // 학교와 나이 설정
//        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
//        val age = currentYear - (friend.birthYear.toIntOrNull() ?: currentYear)
//        holder.schoolAndAgeTextView.text = "${friend.school} • ${age}살"
//
//        // TypeMapper를 통해 색상 정보 가져오기
//        val pokemonType = TypeMapper.getTypeByMbti(friend.mbti)
//
//        // 뱃지 색상 및 텍스트 설정
//        holder.badgeTextView.text = friend.type
//        holder.badgeTextView.setBackgroundColor(android.graphics.Color.parseColor(pokemonType.color))
//
//        // 배경 원 색상 설정
//        holder.imageBackground.setBackgroundColor(android.graphics.Color.parseColor(pokemonType.backgroundColor))
//
//        // 프로필 이미지 설정 (현재 드로어블 이미지 사용)
//        holder.pokemonImageView.setImageResource(getDrawableResIdByType(friend.type))
//    }

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

//        // 배경 원 색상 설정
//        holder.imageBackground.background = GradientDrawable().apply {
//            shape = GradientDrawable.OVAL
//            setColor(Color.parseColor(pokemonType.backgroundColor))
//        }

        // 배경 원 색상 설정 (투명도 포함)
        val backgroundColor = Color.parseColor(pokemonType.backgroundColor)
        val gradientDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(backgroundColor)
        }
        holder.imageBackground.background = gradientDrawable

        // 프로필 이미지 설정
        holder.pokemonImageView.setImageResource(getDrawableResIdByType(friend.type))
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

    // 속성에 따라 드로워블 리소스 반환
    private fun getDrawableResIdByType(type: String): Int {
        return when (type) {
            "물" -> R.drawable.fish // 물 속성 이미지 (드로워블에 있는 파일명)
            "페어리" -> R.drawable.ppippi
            else -> R.drawable.placeholder_image // 기본 이미지
        }
    }
}
