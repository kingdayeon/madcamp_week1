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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Friend

class FriendAdapter(private val friendList: List<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

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

        // 뱃지 색상 및 텍스트 설정
        holder.badgeTextView.text = friend.type
        holder.badgeTextView.setBackgroundColor(android.graphics.Color.parseColor(friend.backgroundColor))

        // 이미지 로드 (드로워블에서 가져오기)
        holder.pokemonImageView.setImageResource(getDrawableResIdByType(friend.type))
    }

    override fun getItemCount(): Int = friendList.size

    // ViewHolder 정의
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        val schoolAndAgeTextView: TextView = itemView.findViewById(R.id.text_school_age)
        val badgeTextView: TextView = itemView.findViewById(R.id.text_badge)
        val pokemonImageView: ImageView = itemView.findViewById(R.id.image_profile)
    }

    // 속성에 따라 드로워블 리소스 반환
    private fun getDrawableResIdByType(type: String): Int {
        return when (type) {
            "물" -> R.drawable.fish // 물 속성 이미지 (드로워블에 있는 파일명)
            else -> R.drawable.placeholder_image // 기본 이미지
        }
    }
}
