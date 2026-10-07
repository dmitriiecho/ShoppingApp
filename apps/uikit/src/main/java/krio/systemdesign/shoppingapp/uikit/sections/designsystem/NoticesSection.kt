package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.ErrorBanner
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun NoticesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("Notice") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_notice_styles)) {
                Notice(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.uikit_sample_notice_error),
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(R.string.uikit_sample_notice_success),
                    style = NoticeStyle.Success,
                )
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_notice_neutral),
                    style = NoticeStyle.Neutral,
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_subtitle)) {
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_notice_neutral),
                    subtitle = stringResource(R.string.uikit_sample_notice_subtitle),
                    style = NoticeStyle.Neutral,
                )
            }
        }
        sampleGroup("NoticeWithAction") {
            SampleVariant {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.uikit_sample_notice_error),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_notice_action),
                    onAction = {},
                )
                NoticeWithAction(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(R.string.uikit_sample_notice_success),
                    style = NoticeStyle.Success,
                    actionText = stringResource(R.string.uikit_sample_notice_action),
                    onAction = {},
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_subtitle)) {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.uikit_sample_notice_error),
                    subtitle = stringResource(R.string.uikit_sample_notice_subtitle),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_notice_action),
                    onAction = {},
                )
            }
        }
        sampleGroup("ErrorBanner") {
            SampleVariant {
                ErrorBanner(
                    message = stringResource(R.string.uikit_sample_load_error),
                    onRetry = {},
                )
            }
        }
    }
}
