package org.automyjsa.automyjsa.ui.compose.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.automyjsa.automyjsa.ui.compose.theme.AutomyjsxTheme

@Composable
fun MySwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors(uncheckedThumbColor= AutomyjsxTheme.colors.switchUncheckedThumbColor)
) {
    Switch(checked, onCheckedChange, modifier, enabled, interactionSource, colors)
}