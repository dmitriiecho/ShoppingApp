package krio.systemdesign.shoppingapp.uikit

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons

// Разделы каталога: значок, название и пояснение для списка разделов. Что показать в разделе, решает SectionScreen.
enum class UiKitSection(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    Theme(AppIcons.Palette, R.string.uikit_section_theme, R.string.uikit_section_theme_description),
    Buttons(AppIcons.SmartButton, R.string.uikit_section_buttons, R.string.uikit_section_buttons_description),
    Notices(
        AppIcons.Feedback,
        R.string.uikit_section_notices,
        R.string.uikit_section_notices_description,
    ),
    Cards(AppIcons.ViewAgenda, R.string.uikit_section_cards, R.string.uikit_section_cards_description),
    Images(AppIcons.Image, R.string.uikit_section_images, R.string.uikit_section_images_description),
    Inputs(AppIcons.TextFields, R.string.uikit_section_inputs, R.string.uikit_section_inputs_description),
    Loading(AppIcons.Downloading, R.string.uikit_section_loading, R.string.uikit_section_loading_description),
    ScreenStates(
        AppIcons.Smartphone,
        R.string.uikit_section_screen_states,
        R.string.uikit_section_screen_states_description,
    ),
    Bars(AppIcons.WebAsset, R.string.uikit_section_bars, R.string.uikit_section_bars_description),
    Dialogs(
        AppIcons.ChatBubble,
        R.string.uikit_section_dialogs,
        R.string.uikit_section_dialogs_description,
    ),
}
