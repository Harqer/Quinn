cat << 'INNER_EOF' > /home/shaolin/lyria/.scratch/patch_vm.py
import re

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt", "r") as f:
    content = f.read()

import_statement = "import com.google.firebase.crashlytics.FirebaseCrashlytics\n"
if "FirebaseCrashlytics" not in content:
    content = content.replace("import javax.inject.Inject\n", "import javax.inject.Inject\n" + import_statement)

# Add the SharedPreferences variable and the init logic
state_flow = """
    private val prefs = context.getSharedPreferences("mave_prefs", Context.MODE_PRIVATE)

    private val _hasAcceptedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_accepted_privacy_policy", false))
    val hasAcceptedPrivacyPolicy: StateFlow<Boolean> = _hasAcceptedPrivacyPolicy.asStateFlow()

    fun acceptPrivacyPolicy() {
        prefs.edit().putBoolean("has_accepted_privacy_policy", true).apply()
        _hasAcceptedPrivacyPolicy.value = true
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
"""
if "hasAcceptedPrivacyPolicy" not in content:
    content = content.replace("private val _isMusicAccountConnected", state_flow + "\n    private val _isMusicAccountConnected")

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt", "w") as f:
    f.write(content)

INNER_EOF
python3 /home/shaolin/lyria/.scratch/patch_vm.py
