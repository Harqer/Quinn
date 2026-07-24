import os
import glob
import re

files = glob.glob('/home/shaolin/lyria/app/src/test/java/com/musically/studio/ui/**/*.kt', recursive=True)
for file in files:
    with open(file, 'r') as f:
        content = f.read()
    
    # Check if file has MainViewModel(FakeApiClient
    if 'MainViewModel(' in content:
        # We need to replace MainViewModel(FakeApiClient(), ...) with MainViewModel(mockContext, FakeApiClient(), ...)
        # But we also need to import android.content.Context
        if 'import android.content.Context' not in content:
            content = content.replace('import org.junit.Test', 'import org.junit.Test\nimport android.content.Context')
            
        content = content.replace('MainViewModel(FakeApiClient()', 'MainViewModel(Mockito.mock(Context::class.java), FakeApiClient()')
        content = content.replace('MainViewModel(fakeApiClient', 'MainViewModel(Mockito.mock(Context::class.java), fakeApiClient')
        
        # In HomeScreenTest.kt, there is an Unresolved reference 'HomeScreen'
        content = content.replace("import com.musically.studio.ui.screens.HomeScreen", "import com.musically.studio.ui.screens.HomeScreen") # No op if already there
        
        with open(file, 'w') as f:
            f.write(content)
