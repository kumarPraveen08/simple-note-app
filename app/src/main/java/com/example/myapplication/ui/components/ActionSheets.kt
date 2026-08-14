package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AppThemeStyle
import com.example.myapplication.ui.theme.AppThemePalettes
import com.example.myapplication.ui.theme.ThemePalettePreview

@Composable
fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    itemModifier: Modifier = Modifier
) {
    Row(
        modifier = itemModifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    itemModifier: Modifier = Modifier
) {
    Row(
        modifier = itemModifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        )
    }
}

@Composable
fun ThemePreviewCard(previewModifier: Modifier = Modifier) {
    Surface(
        modifier = previewModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.18f),
                        radius = size.minDimension * 0.28f,
                        center = Offset(size.width * 0.72f, size.height * 0.42f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.22f),
                        radius = size.minDimension * 0.16f,
                        center = Offset(size.width * 0.28f, size.height * 0.55f)
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {
                    Text(
                        text = "3 notes · pinned",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Note title sample text",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Note preview sample text",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LinearProgressIndicator(
                progress = { 0.62f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}

@Composable
fun ThemePalettePicker(
    selected: AppThemeStyle,
    enabled: Boolean,
    onSelect: (AppThemeStyle) -> Unit,
    pickerModifier: Modifier = Modifier
) {
    LazyRow(
        modifier = pickerModifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(AppThemePalettes, key = { it.style.name }) { palette ->
            val isSelected = palette.style == selected
            Surface(
                onClick = { if (enabled) onSelect(palette.style) },
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    PaletteSwatch(
                        preview = palette.preview,
                        selected = isSelected,
                        dimmed = !enabled
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteSwatch(
    preview: ThemePalettePreview,
    selected: Boolean,
    dimmed: Boolean
) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        ) {
            val halfW = size.width / 2f
            val halfH = size.height / 2f
            val alpha = if (dimmed) 0.45f else 1f
            drawRect(preview.topLeft.copy(alpha = alpha), Offset.Zero, Size(halfW, halfH))
            drawRect(preview.topRight.copy(alpha = alpha), Offset(halfW, 0f), Size(halfW, halfH))
            drawRect(preview.bottomLeft.copy(alpha = alpha), Offset(0f, halfH), Size(halfW, halfH))
            drawRect(
                preview.bottomRight.copy(alpha = alpha),
                Offset(halfW, halfH),
                Size(halfW, halfH)
            )
        }
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

data class SheetQuickAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

data class SheetListAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
fun ActionSheetHeader(
    title: String,
    subtitle: String,
    leading: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    headerModifier: Modifier = Modifier
) {
    Column(headerModifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leading()
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing?.invoke()
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ActionSheetQuickRow(
    actions: List<SheetQuickAction>,
    rowModifier: Modifier = Modifier
) {
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { action ->
            Surface(
                onClick = action.onClick,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(action.icon, contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        action.label,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ActionSheetListItem(
    action: SheetListAction,
    itemModifier: Modifier = Modifier
) {
    Surface(
        onClick = action.onClick,
        modifier = itemModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(action.icon, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(action.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    action.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActionSheetContent(
    title: String,
    subtitle: String,
    leading: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    quickActions: List<SheetQuickAction> = emptyList(),
    listActions: List<SheetListAction> = emptyList(),
    contentModifier: Modifier = Modifier
) {
    Column(
        modifier = contentModifier
            .fillMaxWidth()
            .padding(bottom = 28.dp)
    ) {
        ActionSheetHeader(
            title = title,
            subtitle = subtitle,
            leading = leading,
            trailing = trailing
        )
        if (quickActions.isNotEmpty()) {
            ActionSheetQuickRow(quickActions)
        }
        listActions.forEach { ActionSheetListItem(it) }
    }
}
