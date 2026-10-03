package krio.systemdesign.shoppingapp.uikit

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Announcement
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SmartButton
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.ui.graphics.vector.ImageVector

// Разделы каталога: значок, название и пояснение для списка разделов. Что показать в разделе, решает SectionScreen.
enum class UiKitSection(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    Theme(Icons.Outlined.Palette, R.string.uikit_section_theme, R.string.uikit_section_theme_description),
    Buttons(Icons.Outlined.SmartButton, R.string.uikit_section_buttons, R.string.uikit_section_buttons_description),
    Notices(
        Icons.AutoMirrored.Outlined.Announcement,
        R.string.uikit_section_notices,
        R.string.uikit_section_notices_description,
    ),
    Cards(Icons.Outlined.ViewAgenda, R.string.uikit_section_cards, R.string.uikit_section_cards_description),
    Images(Icons.Outlined.Image, R.string.uikit_section_images, R.string.uikit_section_images_description),
    Inputs(Icons.Outlined.TextFields, R.string.uikit_section_inputs, R.string.uikit_section_inputs_description),
    Loading(Icons.Outlined.Downloading, R.string.uikit_section_loading, R.string.uikit_section_loading_description),
    ScreenStates(
        Icons.Outlined.Smartphone,
        R.string.uikit_section_screen_states,
        R.string.uikit_section_screen_states_description,
    ),
    Bars(Icons.Outlined.WebAsset, R.string.uikit_section_bars, R.string.uikit_section_bars_description),
    Dialogs(
        Icons.Outlined.ChatBubbleOutline,
        R.string.uikit_section_dialogs,
        R.string.uikit_section_dialogs_description,
    ),
}
