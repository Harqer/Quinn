import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

fun testMerge() {
    val a = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.detailPane()
    val b = metadata {
        put(NavDisplay.TransitionKey) {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500)
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        }
    }
    val c = a + b
}
