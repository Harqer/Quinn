import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.liveModel

fun main() {
    println(Firebase.ai.liveModel("gemini-2.0-flash-exp").toString())
}
