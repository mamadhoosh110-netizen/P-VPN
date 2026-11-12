package com.v2ray.ang.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppDivider
import com.v2ray.ang.ui.compose.colorFabActive
import com.v2ray.ang.ui.compose.colorFabInactiveDark
import com.v2ray.ang.ui.compose.colorFabInactiveLight
import kotlinx.coroutines.launch

@Composable
fun MainBottomBar(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    onAction: (MainAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    val statusColor by animateColorAsState(
        targetValue = if (isRunning) Color(0xFF35C98A) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "vpnStatusColor"
    )
    val containerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    LaunchedEffect(isRunning) {
        if (!isRunning) rotationAnim.snapTo(0f)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, containerShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(
                                alpha = if (isDarkTheme) 0.96f else 0.985f
                            )
                        )
                    ),
                    shape = containerShape
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                    shape = containerShape
                )
                .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            AppDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(start = 20.dp, end = 92.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .background(statusColor, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.semantics {
                            contentDescription = displayText
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (!isRunning) {
                    scope.launch {
                        rotationAnim.animateTo(
                            targetValue = 720f,
                            animationSpec = tween(durationMillis = 1800)
                        )
                    }
                }
                onAction(MainAction.ToggleService)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 22.dp)
                .offset(y = (-25).dp)
                .navigationBarsPadding()
                .shadow(8.dp, CircleShape),
            shape = CircleShape,
            containerColor = if (isRunning) colorFabActive
            else if (isDarkTheme) colorFabInactiveDark
            else colorFabInactiveLight
        ) {
            Icon(
                painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                else painterResource(R.drawable.ic_play_24dp),
                contentDescription = stringResource(
                    if (isRunning) R.string.acc_stop else R.string.acc_start
                ),
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer { rotationZ = rotationAnim.value }
            )
        }
    }
}
