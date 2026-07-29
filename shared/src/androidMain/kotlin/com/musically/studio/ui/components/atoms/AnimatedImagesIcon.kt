package com.musically.studio.ui.components.atoms

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val animated_images: ImageVector
  get() {
    if (_animated_images != null) {
      return _animated_images!!
    }
    _animated_images =
      ImageVector.Builder(
          name = "animated_images",
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
            moveTo(12f, 13.5f)
            lineToRelative(6f, -4f)
            lineToRelative(-6f, -4f)
            verticalLineToRelative(8f)
            close()
            moveTo(12.7f, 19f)
            horizontalLineToRelative(5.6f)
            quadToRelative(-0.18f, 0.65f, -0.6f, 1.05f)
            quadToRelative(-0.42f, 0.4f, -1.1f, 0.5f)
            lineTo(5.7f, 21.88f)
            quadTo(4.88f, 22f, 4.21f, 21.49f)
            reflectiveQuadTo(3.45f, 20.15f)
            lineTo(2.13f, 9.23f)
            quadTo(2.03f, 8.4f, 2.53f, 7.75f)
            reflectiveQuadTo(3.85f, 7f)
            lineTo(5f, 6.85f)
            verticalLineToRelative(2f)
            lineTo(4.1f, 8.98f)
            lineTo(5.45f, 19.9f)
            lineTo(12.7f, 19f)
            close()
            moveTo(9f, 17f)
            quadTo(8.18f, 17f, 7.59f, 16.41f)
            reflectiveQuadTo(7f, 15f)
            verticalLineTo(4f)
            quadTo(7f, 3.17f, 7.59f, 2.59f)
            reflectiveQuadTo(9f, 2f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(22f, 4f)
            verticalLineTo(15f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 17f)
            horizontalLineTo(9f)
            close()
            moveTo(9f, 15f)
            horizontalLineTo(20f)
            verticalLineTo(4f)
            horizontalLineTo(9f)
            verticalLineTo(15f)
            close()
            moveTo(14.5f, 9.5f)
            close()
            moveTo(5.45f, 19.9f)
            close()
          }
        }
        .build()
    return _animated_images!!
  }

private var _animated_images: ImageVector? = null
