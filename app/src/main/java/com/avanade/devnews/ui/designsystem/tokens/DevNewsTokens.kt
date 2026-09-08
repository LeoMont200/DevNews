
package com.avanade.devnews.ui.designsystem.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.avanade.devnews.ui.designsystem.theme.OrangePrimary
import com.avanade.devnews.ui.designsystem.theme.PurpleAccent

@Immutable
data class DevNewsSpacing(
    val xSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 32.dp
)

object DevNewsDesignTokens {
    val spacing = DevNewsSpacing()

    val articleImagePlaceholderGradient: Brush =
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF3A435A),
                Color(0xFF161A26)
            )
        )

    val primaryActionGradient: Brush =
        Brush.horizontalGradient(
            colors = listOf(OrangePrimary, PurpleAccent)
        )

    val cardShape = RoundedCornerShape(20.dp)
    val badgeShape = RoundedCornerShape(8.dp)
    val inputShape = RoundedCornerShape(18.dp)
    val buttonShape = RoundedCornerShape(14.dp)
}
