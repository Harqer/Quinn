package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.musically.studio.network.MaveCategory

@Composable
fun CategoryCardsRow(
    categories: List<MaveCategory>,
    onCategoryClick: (String) -> Unit
) {
    val defaultColors = listOf(
        Color(0xFF9333EA), // Purple
        Color(0xFF059669), // Emerald
        Color(0xFFE11D48), // Rose
        Color(0xFF0284C7)  // Sky Blue
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Generate a Vibe",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories.size) { index ->
                val category = categories[index]
                val color = try { category.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } } catch (e: Exception) { null } ?: defaultColors[index % defaultColors.size]
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .clickable { onCategoryClick(category.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
