with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt", "r") as f:
    content = f.read()

state_flows = """
    private val _playlists = MutableStateFlow<List<com.musically.studio.network.MavePlaylist>>(emptyList())
    val playlists: StateFlow<List<com.musically.studio.network.MavePlaylist>> = _playlists.asStateFlow()

    private val _categories = MutableStateFlow<List<com.musically.studio.network.MaveCategory>>(emptyList())
    val categories: StateFlow<List<com.musically.studio.network.MaveCategory>> = _categories.asStateFlow()

    private val _albums = MutableStateFlow<List<com.musically.studio.network.MaveAlbum>>(emptyList())
    val albums: StateFlow<List<com.musically.studio.network.MaveAlbum>> = _albums.asStateFlow()
"""
if "val playlists: StateFlow" not in content:
    content = content.replace("val tracks: StateFlow<List<MaveTrack>> = _tracks.asStateFlow()", "val tracks: StateFlow<List<MaveTrack>> = _tracks.asStateFlow()\n" + state_flows)

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt", "w") as f:
    f.write(content)
