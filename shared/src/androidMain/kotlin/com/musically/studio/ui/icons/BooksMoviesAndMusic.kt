/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for BooksMoviesAndMusic.kt
 */

package com.musically.studio.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val books_movies_and_music: ImageVector
  get() {
    if (_books_movies_and_music != null) {
      return _books_movies_and_music!!
    }
    _books_movies_and_music =
      ImageVector.Builder(
          name = "books_movies_and_music",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(4f, 22f)
            quadTo(3.58f, 22f, 3.29f, 21.71f)
            quadTo(3f, 21.43f, 3f, 21f)
            verticalLineTo(7.05f)
            quadTo(3f, 6.68f, 3.15f, 6.41f)
            reflectiveQuadTo(3.65f, 6f)
            lineToRelative(10f, -4f)
            quadToRelative(0.5f, -0.2f, 0.93f, 0.14f)
            quadTo(15f, 2.47f, 15f, 3f)
            verticalLineTo(6f)
            horizontalLineToRelative(1f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(17f, 7f)
            verticalLineToRelative(3f)
            horizontalLineTo(15f)
            verticalLineTo(8f)
            horizontalLineTo(5f)
            verticalLineTo(20f)
            horizontalLineToRelative(5.18f)
            lineToRelative(2f, 2f)
            horizontalLineTo(4f)
            close()
            moveTo(9f, 6f)
            horizontalLineToRelative(4f)
            verticalLineTo(4.45f)
            lineTo(9f, 6f)
            close()
            moveToRelative(4.46f, 14.54f)
            quadTo(12f, 19.08f, 12f, 17f)
            reflectiveQuadToRelative(1.46f, -3.54f)
            reflectiveQuadTo(17f, 12f)
            reflectiveQuadToRelative(3.54f, 1.46f)
            quadTo(22f, 14.93f, 22f, 17f)
            reflectiveQuadToRelative(-1.46f, 3.54f)
            reflectiveQuadTo(17f, 22f)
            reflectiveQuadTo(13.46f, 20.54f)
            close()
            moveTo(15.75f, 19.5f)
            lineToRelative(4f, -2.5f)
            lineToRelative(-4f, -2.5f)
            verticalLineToRelative(5f)
            close()
            moveTo(5f, 20f)
            verticalLineTo(8f)
            verticalLineToRelative(2f)
            quadToRelative(0f, 0.8f, 0f, 2.75f)
            reflectiveQuadTo(5f, 17f)
            quadToRelative(0f, 0.88f, 0f, 1.54f)
            reflectiveQuadTo(5f, 20f)
            close()
          }
        }
        .build()
    return _books_movies_and_music!!
  }

private var _books_movies_and_music: ImageVector? = null
