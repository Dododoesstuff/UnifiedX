package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppPrimary
import com.example.ui.theme.AppSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppNavTab

data class NavItemData(
    val tab: AppNavTab,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun AeroSuperbarNav(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItemData(AppNavTab.HOME, "Explore", Icons.Default.Explore, "nav_tab_home"),
        NavItemData(AppNavTab.LIBRARY, "Library", Icons.Default.LibraryMusic, "nav_tab_library"),
        NavItemData(AppNavTab.JAM, "Collab", Icons.Default.Groups, "nav_tab_collab"),
        NavItemData(AppNavTab.SETTINGS, "Settings", Icons.Default.Settings, "nav_tab_settings")
    )

    val navShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding()
            .shadow(12.dp, navShape, spotColor = AppPrimary.copy(alpha = 0.2f), ambientColor = Color.Black)
            .clip(navShape)
            .border(
                border = BorderStroke(width = 1.dp, color = AppBorder),
                shape = navShape
            )
            .background(AppSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.tab
                NavTile(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onTabSelected(item.tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTile(
    item: NavItemData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AppPrimary else TextSecondary,
        label = "icon_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) TextPrimary else TextSecondary,
        label = "text_color"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 0.dp,
        animationSpec = spring(),
        label = "indicator"
    )

    val tileShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(tileShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = AppPrimary.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(vertical = 6.dp)
            .testTag(item.testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.title,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(3.dp))

            Box(
                modifier = Modifier
                    .width(indicatorWidth)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(AppPrimary)
            )
        }
    }
}
