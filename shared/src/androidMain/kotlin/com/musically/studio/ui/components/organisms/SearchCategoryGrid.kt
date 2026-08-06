package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.musically.studio.network.MaveCategory
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun SearchCategoryGrid(
    modifier: Modifier = Modifier,
    categories: List<MaveCategory>,
    colors: List<Color>,
    contentPadding: PaddingValues,
    onNavigateToCategory: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(categories.size, key = { categories[it].id }) { index ->
            val category = categories[index]
            val color = try { category.colorHex?.let { Color(it.toColorInt()) } } catch(e: Exception) { null } ?: colors[index % colors.size]
            Box(
                modifier = Modifier
                    .aspectRatio(1.5f)
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
                    .debouncedClickable { onNavigateToCategory(category.id) }
            ) {
                Text(
                    text = category.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
                // Decorative angled box
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 16.dp, y = 8.dp)
                        .size(64.dp)
                        .rotate(25f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                )
            }
        }
    }
}
