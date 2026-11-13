package com.v2ray.ang.ui.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFF000000), // Black
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFE0E0E0), // Light Gray
    onPrimaryContainer = Color(0xFF000000), // Black
    secondary = Color(0xFFf97910), // Orange
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFFFE8D6), // Pale Orange
    onSecondaryContainer = Color(0xFF2B1700), // Dark Brown
    tertiary = Color(0xFF009966), // Green
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFA0F2D0), // Light Green
    onTertiaryContainer = Color(0xFF00201A), // Dark Teal
    error = Color(0xFFBA1A1A), // Red
    errorContainer = Color(0xFFFFDAD6), // Light Red
    onError = Color(0xFFFFFFFF), // White
    onErrorContainer = Color(0xFF410002), // Dark Red
    background = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFF1C1B1F), // Near Black
    surface = Color(0xFFFFFFFF), // White
    onSurface = Color(0xFF1C1B1F), // Near Black
    surfaceVariant = Color(0xFFE7E0EC), // Light Purple Gray
    onSurfaceVariant = Color(0xFF49454F), // Dark Gray
    outline = Color(0xFF79747E), // Medium Gray
    outlineVariant = Color(0xFFCAC4D0), // Light Gray
    inverseSurface = Color(0xFF313033), // Dark Gray
    inverseOnSurface = Color(0xFFF4EFF4), // Very Light Gray
    inversePrimary = Color(0xFFC0C0C0), // Silver Gray
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFF000000), // Black
    surfaceContainerLowest = Color(0xFFFFFFFF), // White
    surfaceContainerLow = Color(0xFFF7F7F7), // Very Light Gray
    surfaceContainer = Color(0xFFF1F1F1), // Light Gray
    surfaceContainerHigh = Color(0xFFEBEBEB), // Light Gray
    surfaceContainerHighest = Color(0xFFE5E5E5), // Light Gray
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFC0C0C0), // Silver Gray
    onPrimary = Color(0xFF303030), // Dark Gray
    primaryContainer = Color(0xFF474747), // Gray
    onPrimaryContainer = Color(0xFFE0E0E0), // Light Gray
    secondary = Color(0xFFf97910), // Orange
    onSecondary = Color(0xFF4E2600), // Dark Brown
    secondaryContainer = Color(0xFF6F3800), // Brown
    onSecondaryContainer = Color(0xFFFFE8D6), // Pale Orange
    tertiary = Color(0xFF83D6B5), // Mint Green
    onTertiary = Color(0xFF00382E), // Dark Teal
    tertiaryContainer = Color(0xFF005143), // Teal
    onTertiaryContainer = Color(0xFFA0F2D0), // Light Green
    error = Color(0xFFFFB4AB), // Light Red
    errorContainer = Color(0xFF93000A), // Dark Red
    onError = Color(0xFF690005), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6), // Light Red
    background = Color(0xFF1C1B1F), // Near Black
    onBackground = Color(0xFFE6E1E5), // Light Gray
    surface = Color(0xFF1C1B1F), // Near Black
    onSurface = Color(0xFFE6E1E5), // Light Gray
    surfaceVariant = Color(0xFF49454F), // Dark Gray
    onSurfaceVariant = Color(0xFFCAC4D0), // Light Gray
    outline = Color(0xFF938F99), // Grayish Purple
    outlineVariant = Color(0xFF49454F), // Dark Gray
    inverseSurface = Color(0xFFE6E1E5), // Light Gray
    inverseOnSurface = Color(0xFF1C1B1F), // Near Black
    inversePrimary = Color(0xFF000000), // Black
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFFC0C0C0), // Silver Gray
    surfaceContainerLowest = Color(0xFF0F0F12), // Near Black
    surfaceContainerLow = Color(0xFF1A191D), // Dark Gray
    surfaceContainer = Color(0xFF1E1D21), // Dark Gray
    surfaceContainerHigh = Color(0xFF282729), // Dark Gray
    surfaceContainerHighest = Color(0xFF333234), // Dark Gray
)

// Semantic Colors
val colorPing = Color(0xFF009966) // Green
val colorPingRed = Color(0xFFFF0099) // Pink Red
val colorConfigType = Color(0xFFf97910) // Orange
val colorFabActive = Color(0xFFf97910) // Orange
val colorFabInactiveLight = Color(0xFF9C9C9C) // Gray
val colorFabInactiveDark = Color(0xFF646464) // Dark Gray
val dividerColorLight = Color(0xFFE0E0E0) // Light Gray
val dividerColorDark = Color(0xFF424242) // Dark Gray

// Toast Colors 85%
val toastNormalBgLight = Color(0xD9353A3E) // Dark Gray
val toastNormalBgDark = Color(0xD94A4F54) // Darker Gray
val toastSuccessBg = Color(0xD9388E3C) // Green
val toastErrorBg = Color(0xD9D50000) // Red
val toastInfoBg = Color(0xD93F51B5) // Indigo Blue
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    private val _pvpnTheme = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_PVPN_THEME, "purple") ?: "purple"
    )
    val pvpnTheme: StateFlow<String> = _pvpnTheme.asStateFlow()

    fun setPvpnTheme(theme: String) {
        val valid = PvpnThemes.colors.containsKey(theme).let { if (it) theme else "purple" }
        MmkvManager.encodeSettings(AppConfig.PREF_PVPN_THEME, valid)
        _pvpnTheme.value = valid
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
        _pvpnTheme.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_PVPN_THEME, "purple") ?: "purple"
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}


val LocalDarkTheme = compositionLocalOf { false }

/**
 * Non-color design tokens for the selected P VPN theme.
 * Screens and components can use these values to change their shape,
 * borders, spacing and glass treatment along with the color palette.
 */
data class PvpnThemeStyle(
    val cardRadius: Dp,
    val cardBorderWidth: Dp,
    val cardGlassAlpha: Float,
    val cardElevation: Dp,
    val useGlow: Boolean
)

private object PvpnThemeStyles {
    val styles = mapOf(
        "purple" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "red" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "ocean" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "emerald" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "gold" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "midnight" to PvpnThemeStyle(16.dp, 0.6.dp, 0.04f, 1.dp, false),
        "aurora" to PvpnThemeStyle(20.dp, 0.8.dp, 0.08f, 2.dp, false),
        "pink" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 2.dp, false),
        "silver" to PvpnThemeStyle(16.dp, 0.6.dp, 0.04f, 1.dp, false),
        "graphite" to PvpnThemeStyle(14.dp, 0.5.dp, 0.03f, 1.dp, false),
        "glass" to PvpnThemeStyle(22.dp, 1.dp, 0.12f, 4.dp, true),
        "neon" to PvpnThemeStyle(18.dp, 1.dp, 0.10f, 3.dp, true),
        "gradient" to PvpnThemeStyle(20.dp, 0.8.dp, 0.10f, 3.dp, true),
        "material" to PvpnThemeStyle(16.dp, 0.6.dp, 0.05f, 2.dp, false),
        "dashboard" to PvpnThemeStyle(14.dp, 0.5.dp, 0.04f, 1.dp, false),
        "luxury" to PvpnThemeStyle(18.dp, 0.8.dp, 0.08f, 3.dp, false),
        "mono_black" to PvpnThemeStyle(14.dp, 0.dp, 0f, 0.dp, false),
        "mono_white" to PvpnThemeStyle(14.dp, 0.dp, 0f, 0.dp, false),
        "mono_green" to PvpnThemeStyle(14.dp, 0.dp, 0f, 0.dp, false),
        "mono_blue" to PvpnThemeStyle(14.dp, 0.dp, 0f, 0.dp, false)
    )
}

val LocalPvpnThemeStyle = compositionLocalOf {
    PvpnThemeStyles.styles.getValue("purple")
}

private object PvpnThemes {
    data class Palette(
        val primary: Color,
        val secondary: Color,
        val tertiary: Color,
        val background: Color,
        val surface: Color
    )

    val colors = mapOf(
        "purple" to Palette(Color(0xFF8B5CF6), Color(0xFF7C3AED), Color(0xFF0891B2), Color(0xFF111827), Color(0xFF1F2937)),
        "red" to Palette(Color(0xFFF87171), Color(0xFFDC2626), Color(0xFFF59E0B), Color(0xFF18181B), Color(0xFF27272A)),
        "ocean" to Palette(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF2DD4BF), Color(0xFF0C1926), Color(0xFF142B3D)),
        "emerald" to Palette(Color(0xFF34D399), Color(0xFF059669), Color(0xFFA3E635), Color(0xFF0B1B16), Color(0xFF153329)),
        "gold" to Palette(Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFFFDE68A), Color(0xFF1C1917), Color(0xFF292524)),
        "midnight" to Palette(Color(0xFF93C5FD), Color(0xFF3B82F6), Color(0xFFC4B5FD), Color(0xFF111827), Color(0xFF1E293B)),
        "aurora" to Palette(Color(0xFF5EEAD4), Color(0xFF0D9488), Color(0xFF7DD3FC), Color(0xFF0F172A), Color(0xFF173344)),
        "pink" to Palette(Color(0xFFF9A8D4), Color(0xFFDB2777), Color(0xFFFBCFE8), Color(0xFF1F1720), Color(0xFF302131)),
        "silver" to Palette(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFFCBD5E1), Color(0xFF111827), Color(0xFF1F2937)),
        "graphite" to Palette(Color(0xFFD1D5DB), Color(0xFF6B7280), Color(0xFF9CA3AF), Color(0xFF111111), Color(0xFF202020)),
        "glass" to Palette(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF2DD4BF), Color(0xFF101827), Color(0xFF1E293B)),
        "neon" to Palette(Color(0xFFA3E635), Color(0xFF65A30D), Color(0xFF22D3EE), Color(0xFF101510), Color(0xFF1C281C)),
        "gradient" to Palette(Color(0xFFA78BFA), Color(0xFF6366F1), Color(0xFF38BDF8), Color(0xFF15152B), Color(0xFF242444)),
        "material" to Palette(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF34D399), Color(0xFF111827), Color(0xFF1F2937)),
        "dashboard" to Palette(Color(0xFF2DD4BF), Color(0xFF0F766E), Color(0xFF93C5FD), Color(0xFF111827), Color(0xFF1E293B)),
        "luxury" to Palette(Color(0xFFFCD34D), Color(0xFFB7791F), Color(0xFFE5E7EB), Color(0xFF18181B), Color(0xFF27272A)),
        "mono_black" to Palette(Color(0xFF374151), Color(0xFF111827), Color(0xFF6B7280), Color(0xFF111827), Color(0xFF1F2937)),
        "mono_white" to Palette(Color(0xFF475569), Color(0xFF334155), Color(0xFF64748B), Color(0xFFF8FAFC), Color(0xFFFFFFFF)),
        "mono_green" to Palette(Color(0xFF22C55E), Color(0xFF16A34A), Color(0xFF86EFAC), Color(0xFF071A10), Color(0xFF102A1B)),
        "mono_blue" to Palette(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF93C5FD), Color(0xFF0F172A), Color(0xFF1E293B))
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val selectedPvpnTheme by ThemeManager.pvpnTheme.collectAsState()
    val palette = PvpnThemes.colors[selectedPvpnTheme] ?: PvpnThemes.colors.getValue("purple")
    // The selected P VPN theme always controls the complete Material color system.
    // Android dynamic colors are intentionally not allowed to replace the user's
    // selected palette, so the same theme identity is retained in light and dark mode.
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = Color(0xFF100B1B),
            primaryContainer = palette.secondary.copy(alpha = 0.28f),
            onPrimaryContainer = palette.primary,
            secondary = palette.secondary,
            onSecondary = Color(0xFF100B1B),
            secondaryContainer = palette.secondary.copy(alpha = 0.22f),
            onSecondaryContainer = Color(0xFFF7F3FC),
            tertiary = palette.tertiary,
            onTertiary = Color(0xFF071311),
            background = palette.background,
            onBackground = Color(0xFFF4F1F8),
            surface = palette.surface,
            onSurface = Color(0xFFF4F1F8),
            surfaceVariant = palette.surface.copy(alpha = 0.94f),
            onSurfaceVariant = Color(0xFFD0C9D8),
            outline = palette.secondary.copy(alpha = 0.58f),
            outlineVariant = palette.secondary.copy(alpha = 0.22f),
            surfaceTint = palette.primary,
            surfaceContainerLowest = palette.background,
            surfaceContainerLow = palette.surface.copy(alpha = 0.72f),
            surfaceContainer = palette.surface,
            surfaceContainerHigh = palette.surface.copy(alpha = 0.94f),
            surfaceContainerHighest = palette.surface
        )
    } else {
        lightColorScheme(
            primary = palette.secondary,
            onPrimary = Color.White,
            primaryContainer = palette.secondary.copy(alpha = 0.15f),
            onPrimaryContainer = palette.secondary,
            secondary = palette.primary,
            onSecondary = Color(0xFF17131D),
            secondaryContainer = palette.primary.copy(alpha = 0.14f),
            onSecondaryContainer = Color(0xFF201B26),
            tertiary = palette.tertiary,
            onTertiary = Color(0xFF071311),
            background = Color(0xFFF7F5FA),
            onBackground = Color(0xFF201B26),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF201B26),
            surfaceVariant = Color(0xFFF0EAF5),
            onSurfaceVariant = Color(0xFF514A58),
            outline = palette.secondary.copy(alpha = 0.55f),
            outlineVariant = palette.secondary.copy(alpha = 0.20f),
            surfaceTint = palette.primary,
            surfaceContainerLowest = Color.White,
            surfaceContainerLow = Color(0xFFFAF7FC),
            surfaceContainer = Color(0xFFF4F0F7),
            surfaceContainerHigh = Color(0xFFEFEAF3),
            surfaceContainerHighest = Color(0xFFE9E3EE)
        )
    }
    val selectedThemeStyle =
        PvpnThemeStyles.styles[selectedPvpnTheme] ?: PvpnThemeStyles.styles.getValue("purple")
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalPvpnThemeStyle provides selectedThemeStyle,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
