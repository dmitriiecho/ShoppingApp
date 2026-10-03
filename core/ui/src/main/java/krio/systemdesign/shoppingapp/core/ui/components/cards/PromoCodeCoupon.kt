package krio.systemdesign.shoppingapp.core.ui.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme


@Composable
fun PromoCodeCoupon(
    code: String,
    discountPercent: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.small
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier
                // Drawn over the Surface background, inset by half the stroke so the edge isn't clipped.
                .drawBehind {
                    val strokeWidth = COUPON_BORDER_WIDTH.toPx()
                    val radius = ShapeRadius.Small.toPx() - strokeWidth / 2
                    drawRoundRect(
                        color = colors.primary,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        cornerRadius = CornerRadius(radius),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                        ),
                    )
                }
                .padding(start = 14.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = code,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.core_ui_discount_percent, discountPercent),
                modifier = Modifier
                    .background(colors.primary, MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                color = colors.onPrimary,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

// Coupon placeholder while codes load. Same size as a coupon with a six-character code,
// so the layout doesn't jump when the codes appear.
@Composable
fun PromoCodeCouponPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            // Like the clickable coupon, take at least 48 dp of height but draw at the coupon's size.
            .minimumInteractiveComponentSize()
            .size(width = PLACEHOLDER_COUPON_WIDTH, height = PLACEHOLDER_COUPON_HEIGHT)
            .shimmerShape(MaterialTheme.shapes.small),
    )
}

private val COUPON_BORDER_WIDTH = 1.5.dp

// Size of a coupon with a six-character code, e.g. SALE10.
private val PLACEHOLDER_COUPON_WIDTH = 130.dp
private val PLACEHOLDER_COUPON_HEIGHT = 36.dp

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PromoCodeCouponPreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PromoCodeCoupon(code = "SALE10", discountPercent = 10, onClick = {})
                    PromoCodeCoupon(code = "SALE25", discountPercent = 25, onClick = {})
                }
                ShimmerPlaceholder {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) {
                            PromoCodeCouponPlaceholder()
                        }
                    }
                }
            }
        }
    }
}
