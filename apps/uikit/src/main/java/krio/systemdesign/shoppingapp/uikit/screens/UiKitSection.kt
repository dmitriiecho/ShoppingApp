package krio.systemdesign.shoppingapp.uikit.screens

import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ChatBubble
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.DesignServices
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Downloading
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.EmojiSymbols
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Extension
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Feedback
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Image
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Palette
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Receipt
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingBag
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SmartButton
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Smartphone
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.TextFields
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ViewAgenda
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.WebAsset
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Widgets
import krio.systemdesign.shoppingapp.uikit.R

// The UI kit's sections in three groups by the module the components live in; SectionScreen decides
// what each one shows. @Keep: the enum travels in a navigation route, and its serializer is found by name.
@Keep
enum class UiKitSection(
    val group: Group,
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    Theme(Group.DesignSystem, AppIcons.Palette, R.string.uikit_section_theme, R.string.uikit_section_theme_description),
    Icons(
        Group.DesignSystem,
        AppIcons.EmojiSymbols,
        R.string.uikit_section_icons,
        R.string.uikit_section_icons_description,
    ),
    Buttons(
        Group.DesignSystem,
        AppIcons.SmartButton,
        R.string.uikit_section_buttons,
        R.string.uikit_section_buttons_description,
    ),
    Notices(
        Group.DesignSystem,
        AppIcons.Feedback,
        R.string.uikit_section_notices,
        R.string.uikit_section_notices_description,
    ),
    Cards(
        Group.DesignSystem,
        AppIcons.ViewAgenda,
        R.string.uikit_section_cards,
        R.string.uikit_section_cards_description,
    ),
    Inputs(
        Group.DesignSystem,
        AppIcons.TextFields,
        R.string.uikit_section_inputs,
        R.string.uikit_section_inputs_description,
    ),
    Loading(
        Group.DesignSystem,
        AppIcons.Downloading,
        R.string.uikit_section_loading,
        R.string.uikit_section_loading_description,
    ),
    ScreenStates(
        Group.DesignSystem,
        AppIcons.Smartphone,
        R.string.uikit_section_screen_states,
        R.string.uikit_section_screen_states_description,
    ),
    Bars(Group.DesignSystem, AppIcons.WebAsset, R.string.uikit_section_bars, R.string.uikit_section_bars_description),
    Dialogs(
        Group.DesignSystem,
        AppIcons.ChatBubble,
        R.string.uikit_section_dialogs,
        R.string.uikit_section_dialogs_description,
    ),

    Product(Group.Shared, AppIcons.Image, R.string.uikit_section_product, R.string.uikit_section_product_description),
    Cart(Group.Shared, AppIcons.ShoppingCart, R.string.uikit_section_cart, R.string.uikit_section_cart_description),
    Order(Group.Shared, AppIcons.Receipt, R.string.uikit_section_order, R.string.uikit_section_order_description),
    Promo(
        Group.Shared,
        AppIcons.ConfirmationNumber,
        R.string.uikit_section_promo,
        R.string.uikit_section_promo_description,
    ),

    CatalogFeature(
        Group.Feature,
        AppIcons.Storefront,
        R.string.uikit_section_catalog_feature,
        R.string.uikit_section_catalog_feature_description,
    ),
    CartFeature(
        Group.Feature,
        AppIcons.ShoppingCart,
        R.string.uikit_section_cart_feature,
        R.string.uikit_section_cart_feature_description,
    ),
    PromoFeature(
        Group.Feature,
        AppIcons.ConfirmationNumber,
        R.string.uikit_section_promo_feature,
        R.string.uikit_section_promo_feature_description,
    ),
    CheckoutFeature(
        Group.Feature,
        AppIcons.ShoppingBag,
        R.string.uikit_section_checkout_feature,
        R.string.uikit_section_checkout_feature_description,
    ),
    ;

    // Where the section's components live: :core:designsystem, :shared:ui or a feature's ui module.
    // @Keep for the same reason: it travels in a navigation route too.
    @Keep
    enum class Group(
        val icon: ImageVector,
        @StringRes val titleRes: Int,
        @StringRes val descriptionRes: Int,
    ) {
        DesignSystem(
            AppIcons.DesignServices,
            R.string.uikit_group_design_system,
            R.string.uikit_group_design_system_description,
        ),
        Shared(AppIcons.Widgets, R.string.uikit_group_shared, R.string.uikit_group_shared_description),
        Feature(AppIcons.Extension, R.string.uikit_group_feature, R.string.uikit_group_feature_description),
    }
}
