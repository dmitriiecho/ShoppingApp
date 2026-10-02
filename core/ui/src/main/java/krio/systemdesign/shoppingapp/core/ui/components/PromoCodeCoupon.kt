package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import krio.systemdesign.shoppingapp.core.ui.R

// Промокод в виде купона: пунктирная рамка, моноширинный код и плашка со скидкой.
@Composable
fun PromoCodeCoupon(
    code: String,
    discountPercent: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(COUPON_CORNER_RADIUS)
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier
                // Рамка рисуется внутри Surface, поверх его фона. Сдвиг на полтолщины — чтобы линия не обрезалась по краю.
                .drawBehind {
                    val strokeWidth = COUPON_BORDER_WIDTH.toPx()
                    val radius = COUPON_CORNER_RADIUS.toPx() - strokeWidth / 2
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
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.core_ui_discount_percent, discountPercent),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onPrimary,
                modifier = Modifier
                    .background(colors.primary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

// Заглушка купона внутри ShimmerPlaceholder, пока коды грузятся. Того же размера, что купон с кодом
// из шести символов, поэтому место под купонами не меняет высоту, когда коды загрузились.
@Composable
fun PromoCodeCouponPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            // Купон — нажимаемый Surface: места он занимает не меньше 48 dp в высоту, а рисуется
            // по своему содержимому. Заглушка занимает место так же.
            .minimumInteractiveComponentSize()
            .size(width = PLACEHOLDER_COUPON_WIDTH, height = PLACEHOLDER_COUPON_HEIGHT)
            .shimmerShape(RoundedCornerShape(COUPON_CORNER_RADIUS)),
    )
}

private val COUPON_CORNER_RADIUS = 10.dp
private val COUPON_BORDER_WIDTH = 1.5.dp

// Размер купона с кодом из шести символов, например SALE10: высота — строка кода и отступы 8 dp сверху и снизу.
private val PLACEHOLDER_COUPON_WIDTH = 130.dp
private val PLACEHOLDER_COUPON_HEIGHT = 36.dp
