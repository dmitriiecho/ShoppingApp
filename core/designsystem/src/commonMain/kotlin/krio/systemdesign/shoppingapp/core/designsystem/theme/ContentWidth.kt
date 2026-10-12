package krio.systemdesign.shoppingapp.core.designsystem.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// The widest a screen's content gets. On a tablet or in landscape the content stays a column of this width in the
// middle instead of stretching edge to edge; on a phone held upright it's narrower anyway.
val ContentMaxWidth = 600.dp

// Makes the element as wide as the width it's given, up to ContentMaxWidth, and centers it. Put it inside the
// scrolling container (after verticalScroll, on a list's items), so the screen also scrolls beside the column.
fun Modifier.contentWidth(): Modifier = centeredWidth(ContentMaxWidth)

// For a top bar over the column: the column with ScreenPadding on both sides, so the title and the buttons line up
// with the content the way they do on a phone, where the bar spans the screen.
fun Modifier.contentBarWidth(): Modifier = centeredWidth(ContentMaxWidth + Spacing.ScreenPadding * 2)

private fun Modifier.centeredWidth(maxWidth: Dp): Modifier = fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .widthIn(max = maxWidth)
    // Fills the width, even an element that would take less on its own (a text field's default width).
    .fillMaxWidth()
