package com.musically.studio.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val ToggleOff: ImageVector
  get() {
    if (_toggle_off != null) {
      return _toggle_off!!
    }
    _toggle_off =
      ImageVector.Builder(
          name = "toggle_off",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 960f,
          viewportHeight = 960f,
        )
        .apply {
          group(translationY = 960f) {
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
              moveTo(280f, -240f)
              quadToRelative(-100f, 0f, -170f, -70f)
              reflectiveQuadTo(40f, -480f)
              quadToRelative(0f, -100f, 70f, -170f)
              reflectiveQuadToRelative(170f, -70f)
              horizontalLineToRelative(400f)
              quadToRelative(100f, 0f, 170f, 70f)
              reflectiveQuadToRelative(70f, 170f)
              quadToRelative(0f, 100f, -70f, 170f)
              reflectiveQuadToRelative(-170f, 70f)
              horizontalLineTo(280f)
              close()
              moveToRelative(0f, -80f)
              horizontalLineToRelative(400f)
              quadToRelative(66f, 0f, 113f, -47f)
              reflectiveQuadToRelative(47f, -113f)
              quadToRelative(0f, -66f, -47f, -113f)
              reflectiveQuadToRelative(-113f, -47f)
              horizontalLineTo(280f)
              quadToRelative(-66f, 0f, -113f, 47f)
              reflectiveQuadToRelative(-47f, 113f)
              quadToRelative(0f, 66f, 47f, 113f)
              reflectiveQuadToRelative(113f, 47f)
              close()
              moveToRelative(85f, -75f)
              quadToRelative(35f, -35f, 35f, -85f)
              reflectiveQuadToRelative(-35f, -85f)
              quadToRelative(-35f, -35f, -85f, -35f)
              reflectiveQuadToRelative(-85f, 35f)
              quadToRelative(-35f, 35f, -35f, 85f)
              reflectiveQuadToRelative(35f, 85f)
              quadToRelative(35f, 35f, 85f, 35f)
              reflectiveQuadToRelative(85f, -35f)
              close()
              moveToRelative(115f, -85f)
              close()
            }
          }
        }
        .build()
    return _toggle_off!!
  }

private var _toggle_off: ImageVector? = null
