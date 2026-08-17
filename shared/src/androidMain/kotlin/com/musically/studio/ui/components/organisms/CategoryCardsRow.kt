/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for CategoryCardsRow.kt
 */

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
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun CategoryCardsRow(
    modifier: Modifier = Modifier,
    categories: List<MaveCategory>,
    onCategoryClick: (String) -> Unit
) {
    val defaultColors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.errorContainer
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Generate a Vibe",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories.size, key = { categories[it].id }) { index ->
                val category = categories[index]
                val color = try { category.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } } catch (e: Exception) { null } ?: defaultColors[index % defaultColors.size]
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color)
                        .debouncedClickable { onCategoryClick(category.id) },
                    contentAlignment = Alignment.Center
                ) {
                    val onColors = listOf(
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer,
                        MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = category.name,
                        color = onColors[index % onColors.size],
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
