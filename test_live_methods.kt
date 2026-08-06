import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.liveModel
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun main() {
    val model = Firebase.ai.liveModel("gemini-2.0-flash-exp")
    println("Methods:")
    model::class.memberFunctions.forEach { println(it.name) }
    println("Properties:")
    model::class.memberProperties.forEach { println(it.name) }
}
