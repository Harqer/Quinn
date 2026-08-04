#!/bin/bash
cat << 'INNER_EOF' >> shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Firebase.kt

fun MainViewModel.fetchCategories() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching categories")
        dataConnectRepository.getCategories()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch categories")
                _catalogErrorMessage.value = e.message
                _isLoading.value = false
            }
            .collectLatest { items ->
                val maveCategories = items.map { item ->
                    MaveCategory(
                        id = item.id,
                        name = item.name,
                        colorHex = null, // Backend doesn't provide this currently, UI has fallback
                        imageUrl = null
                    )
                }
                _categories.value = maveCategories
                _isLoading.value = false
            }
    }
}
INNER_EOF
sed -i 's/fetchLikedTracks()/fetchLikedTracks()\n        fetchCategories()/' shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Firebase.kt
chmod +x update_categories.sh
./update_categories.sh
