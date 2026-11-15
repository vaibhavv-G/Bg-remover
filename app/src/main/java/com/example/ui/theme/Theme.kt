package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
  primary = VioletPrimary,
  onPrimary = PureWhite,
  primaryContainer = VioletSoft,
  onPrimaryContainer = VioletPrimary,
  secondary = BlueAccent,
  onSecondary = PureWhite,
  secondaryContainer = IndigoSoft,
  onSecondaryContainer = BlueAccent,
  tertiary = VioletVibrant,
  onTertiary = PureWhite,
  background = PureWhite,
  onBackground = SlateInk,
  surface = PureWhite,
  onSurface = SlateInk,
  surfaceVariant = SlateSurface,
  onSurfaceVariant = SlateBody,
  outline = SlateBorder,
  outlineVariant = Color(0xFFF1F5F9),
  error = RoseError,
  onError = PureWhite,
  errorContainer = RoseSoft,
  onErrorContainer = RoseError,
)

private val DarkColorScheme = darkColorScheme(
  primary = VioletLight,
  onPrimary = Color(0xFF1E1038),
  primaryContainer = Color(0xFF3B1F7A),
  onPrimaryContainer = VioletSoft,
  secondary = BlueLight,
  onSecondary = Color(0xFF0A224A),
  secondaryContainer = Color(0xFF1E3A8A),
  onSecondaryContainer = IndigoSoft,
  tertiary = VioletLight,
  onTertiary = Color(0xFF1E1038),
  background = DarkBackground,
  onBackground = Color(0xFFF8FAFC),
  surface = DarkSurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = DarkBorder,
  outlineVariant = Color(0xFF1E293B),
  error = Color(0xFFFCA5A5),
  onError = Color(0xFF450A0A),
  errorContainer = Color(0xFF7F1D1D),
  onErrorContainer = RoseSoft,
)

val AppShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(22.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun BGRemoverProTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    shapes = AppShapes,
    content = content,
  )
}
