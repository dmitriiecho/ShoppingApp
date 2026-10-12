package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.ErrorBanner
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_load_error
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_action
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_error
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_neutral
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_subtitle
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_success
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_notice_styles
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_with_subtitle
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NoticesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("Notice") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_notice_styles)) {
                Notice(
                    icon = AppIcons.Error,
                    title = stringResource(Res.string.uikit_sample_notice_error),
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(Res.string.uikit_sample_notice_success),
                    style = NoticeStyle.Success,
                )
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(Res.string.uikit_sample_notice_neutral),
                    style = NoticeStyle.Neutral,
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_subtitle)) {
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(Res.string.uikit_sample_notice_neutral),
                    subtitle = stringResource(Res.string.uikit_sample_notice_subtitle),
                    style = NoticeStyle.Neutral,
                )
            }
        }
        sampleGroup("NoticeWithAction") {
            SampleVariant {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(Res.string.uikit_sample_notice_error),
                    style = NoticeStyle.Error,
                    actionText = stringResource(Res.string.uikit_sample_notice_action),
                    onAction = {},
                )
                NoticeWithAction(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(Res.string.uikit_sample_notice_success),
                    style = NoticeStyle.Success,
                    actionText = stringResource(Res.string.uikit_sample_notice_action),
                    onAction = {},
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_subtitle)) {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(Res.string.uikit_sample_notice_error),
                    subtitle = stringResource(Res.string.uikit_sample_notice_subtitle),
                    style = NoticeStyle.Error,
                    actionText = stringResource(Res.string.uikit_sample_notice_action),
                    onAction = {},
                )
            }
        }
        sampleGroup("ErrorBanner") {
            SampleVariant {
                ErrorBanner(
                    message = stringResource(Res.string.uikit_sample_load_error),
                    onRetry = {},
                )
            }
        }
    }
}
