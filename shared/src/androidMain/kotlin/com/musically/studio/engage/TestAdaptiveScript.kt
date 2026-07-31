import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo

fun main() {
    val clazz = Class.forName("androidx.compose.material3.adaptive.WindowAdaptiveInfoKt")
    clazz.methods.forEach { println(it.name) }
}
