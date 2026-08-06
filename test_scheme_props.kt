import com.google.android.material.color.utilities.Scheme

fun test() {
    val scheme = Scheme.dark(0xFF0000)
    println(scheme.primary)
    println(scheme.onPrimary)
    println(scheme.secondary)
    println(scheme.error)
    println(scheme.surface)
    println(scheme.background)
}
