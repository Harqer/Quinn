/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for DiscoverCategoriesGrid.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.musically.studio.network.MaveCategory
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.theme.MaveBlue500
import com.musically.studio.ui.theme.MaveOrange500
import com.musically.studio.ui.theme.MavePurple700
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun DiscoverCategoriesGrid(
    modifier: Modifier = Modifier,
    categories: List<MaveCategory>,
    onNavigateToCategory: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 24.dp)) {
        val padding = PaddingValues(horizontal = 24.dp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(padding).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
        
        val colors = listOf(MaveOrange500, MaveBlue500, MavePurple700)
        
        Column(modifier = Modifier.padding(padding)) {
            for (i in categories.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val color1 = try { categories[i].colorHex?.let { hex -> Color(hex.toColorInt()) } } catch (e: Exception) { null } ?: colors[(i/2) % colors.size]
                    val interactionSource1 = remember { MutableInteractionSource() }
                    val styleState1 = rememberUpdatedStyleState(interactionSource1) { it.isEnabled = true }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(16f/9f)
                            .background(color1)
                            .debouncedClickable(interactionSource = interactionSource1, indication = null) {
                                onNavigateToCategory(categories[i].id)
                            }
                            .styleable(styleState1, MaveStyles.categoryGridItemStyle)
                    ) {
                        Text(categories[i].name, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    if (i + 1 < categories.size) {
                        val color2 = try { categories[i+1].colorHex?.let { hex -> Color(hex.toColorInt()) } } catch (e: Exception) { null } ?: colors[((i+1)/2) % colors.size]
                        val interactionSource2 = remember { MutableInteractionSource() }
                        val styleState2 = rememberUpdatedStyleState(interactionSource2) { it.isEnabled = true }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(16f/9f)
                                .background(color2)
                                .debouncedClickable(interactionSource = interactionSource2, indication = null) {
                                    onNavigateToCategory(categories[i+1].id)
                                }
                                .styleable(styleState2, MaveStyles.categoryGridItemStyle)
                        ) {
                            Text(categories[i+1].name, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
