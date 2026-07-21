package com.devlomi.tahaqqaqhadith.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.TextUnit

/*
This is used to remove vertical padding on the NotoSans Arabic font.
The function takes a TextUnit (font size) and adjusts the layout to crop the vertical padding, centering the text within the new height.
 */
fun Modifier.cropVerticalPadding(fontSize: TextUnit): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    // Manually force a tighter height constraint based on the actual font scale
    val targetHeight = fontSize.toPx().toInt()

    layout(placeable.width, targetHeight) {
        // Center the font glyph inside your new cropped height bounding box
        val yOffset = (targetHeight - placeable.height) / 2
        placeable.placeRelative(0, yOffset)
    }
}
