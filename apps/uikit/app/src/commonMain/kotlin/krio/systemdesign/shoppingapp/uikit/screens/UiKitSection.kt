package krio.systemdesign.shoppingapp.uikit.screens

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
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_design_system
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_design_system_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_feature
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_feature_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_shared
import krio.systemdesign.shoppingapp.uikit.resources.uikit_group_shared_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_bars
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_bars_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_buttons
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_buttons_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cards
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cards_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cart
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cart_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cart_feature
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_cart_feature_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_catalog_feature
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_catalog_feature_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_checkout_feature
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_checkout_feature_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_dialogs
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_dialogs_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_icons
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_icons_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_inputs
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_inputs_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_loading
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_loading_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_notices
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_notices_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_order
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_order_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_product
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_product_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_promo
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_promo_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_promo_feature
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_promo_feature_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_screen_states
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_screen_states_description
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_theme
import krio.systemdesign.shoppingapp.uikit.resources.uikit_section_theme_description
import org.jetbrains.compose.resources.StringResource

// The UI kit's sections in three groups by the module the components live in; SectionScreen decides
// what each one shows.
internal enum class UiKitSection(
    val group: Group,
    val icon: ImageVector,
    val title: StringResource,
    val description: StringResource,
) {
    Theme(
        Group.DesignSystem,
        AppIcons.Palette,
        Res.string.uikit_section_theme,
        Res.string.uikit_section_theme_description,
    ),
    Icons(
        Group.DesignSystem,
        AppIcons.EmojiSymbols,
        Res.string.uikit_section_icons,
        Res.string.uikit_section_icons_description,
    ),
    Buttons(
        Group.DesignSystem,
        AppIcons.SmartButton,
        Res.string.uikit_section_buttons,
        Res.string.uikit_section_buttons_description,
    ),
    Notices(
        Group.DesignSystem,
        AppIcons.Feedback,
        Res.string.uikit_section_notices,
        Res.string.uikit_section_notices_description,
    ),
    Cards(
        Group.DesignSystem,
        AppIcons.ViewAgenda,
        Res.string.uikit_section_cards,
        Res.string.uikit_section_cards_description,
    ),
    Inputs(
        Group.DesignSystem,
        AppIcons.TextFields,
        Res.string.uikit_section_inputs,
        Res.string.uikit_section_inputs_description,
    ),
    Loading(
        Group.DesignSystem,
        AppIcons.Downloading,
        Res.string.uikit_section_loading,
        Res.string.uikit_section_loading_description,
    ),
    ScreenStates(
        Group.DesignSystem,
        AppIcons.Smartphone,
        Res.string.uikit_section_screen_states,
        Res.string.uikit_section_screen_states_description,
    ),
    Bars(
        Group.DesignSystem,
        AppIcons.WebAsset,
        Res.string.uikit_section_bars,
        Res.string.uikit_section_bars_description,
    ),
    Dialogs(
        Group.DesignSystem,
        AppIcons.ChatBubble,
        Res.string.uikit_section_dialogs,
        Res.string.uikit_section_dialogs_description,
    ),

    Product(
        Group.Shared,
        AppIcons.Image,
        Res.string.uikit_section_product,
        Res.string.uikit_section_product_description,
    ),
    Cart(Group.Shared, AppIcons.ShoppingCart, Res.string.uikit_section_cart, Res.string.uikit_section_cart_description),
    Order(Group.Shared, AppIcons.Receipt, Res.string.uikit_section_order, Res.string.uikit_section_order_description),
    Promo(
        Group.Shared,
        AppIcons.ConfirmationNumber,
        Res.string.uikit_section_promo,
        Res.string.uikit_section_promo_description,
    ),

    CatalogFeature(
        Group.Feature,
        AppIcons.Storefront,
        Res.string.uikit_section_catalog_feature,
        Res.string.uikit_section_catalog_feature_description,
    ),
    CartFeature(
        Group.Feature,
        AppIcons.ShoppingCart,
        Res.string.uikit_section_cart_feature,
        Res.string.uikit_section_cart_feature_description,
    ),
    PromoFeature(
        Group.Feature,
        AppIcons.ConfirmationNumber,
        Res.string.uikit_section_promo_feature,
        Res.string.uikit_section_promo_feature_description,
    ),
    CheckoutFeature(
        Group.Feature,
        AppIcons.ShoppingBag,
        Res.string.uikit_section_checkout_feature,
        Res.string.uikit_section_checkout_feature_description,
    ),
    ;

    // Where the section's components live: :core:designsystem, :crossfeature:ui or a feature's ui module.
    enum class Group(
        val icon: ImageVector,
        val title: StringResource,
        val description: StringResource,
    ) {
        DesignSystem(
            AppIcons.DesignServices,
            Res.string.uikit_group_design_system,
            Res.string.uikit_group_design_system_description,
        ),
        Shared(AppIcons.Widgets, Res.string.uikit_group_shared, Res.string.uikit_group_shared_description),
        Feature(AppIcons.Extension, Res.string.uikit_group_feature, Res.string.uikit_group_feature_description),
    }
}
