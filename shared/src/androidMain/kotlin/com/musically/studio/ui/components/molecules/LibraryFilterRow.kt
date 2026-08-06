package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.FilterPill
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun LibraryFilterRow(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    onSortClick: () -> Unit = {},
    onViewToggleClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.debouncedClickable { onSortClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Sort",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recently played",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            FilterPill(
                text = "Liked",
                isSelected = selectedFilter == "Liked",
                onClick = {
                    onFilterSelected(if (selectedFilter == "Liked") null else "Liked")
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterPill(
                text = "Bookmarks",
                isSelected = selectedFilter == "Bookmarks",
                onClick = {
                    onFilterSelected(if (selectedFilter == "Bookmarks") null else "Bookmarks")
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterPill(
                text = "Downloads",
                isSelected = selectedFilter == "Downloads",
                onClick = {
                    onFilterSelected(if (selectedFilter == "Downloads") null else "Downloads")
                }
            )
        }
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "View as list",
            tint = Color.White,
            modifier = Modifier.size(24.dp).debouncedClickable { onViewToggleClick() }
        )
    }
}
