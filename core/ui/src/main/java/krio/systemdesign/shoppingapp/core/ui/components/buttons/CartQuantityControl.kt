package krio.systemdesign.shoppingapp.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun CartQuantityControl(
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemoveAll: () -> Unit,
    modifier: Modifier = Modifier,
    canIncrease: Boolean = true,
) {
    val controlHeight = ButtonDefaults.MinHeight
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    if (quantity <= 0) {
        WithoutTouchTargetReserve {
            Button(
                onClick = onAdd,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = controlHeight),
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.core_ui_add_to_cart))
            }
        }
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = controlHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(percent = 50),
            color = containerColor,
            contentColor = contentColor,
        ) {
            Row(
                modifier = Modifier.heightIn(min = controlHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CartControlIconButton(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = stringResource(R.string.core_ui_decrease_quantity),
                    onClick = onDecrease,
                    size = controlHeight,
                    contentColor = contentColor,
                )
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.widthIn(min = 24.dp),
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                )
                CartControlIconButton(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.core_ui_increase_quantity),
                    onClick = onIncrease,
                    size = controlHeight,
                    contentColor = contentColor,
                    enabled = canIncrease,
                )
            }
        }
        CartControlIconButton(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.core_ui_remove_from_cart),
            onClick = onRemoveAll,
            size = controlHeight,
            containerColor = containerColor,
            contentColor = contentColor,
        )
    }
}

// Shimmer placeholder for CartQuantityControl, same size and shape as the "Add to cart" button.
@Composable
fun CartQuantityControlPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonDefaults.MinHeight)
            .shimmerShape(RoundedCornerShape(percent = 50)),
    )
}

@Composable
private fun CartControlIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            // Catch taps on a disabled button, or they fall through to the card and open the product.
            .then(if (enabled) Modifier else Modifier.pointerInput(Unit) { detectTapGestures {} }),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(20.dp)
                .alpha(if (enabled) 1f else DISABLED_ALPHA),
            tint = contentColor,
        )
    }
}

// Alpha of disabled elements in Material 3.
private const val DISABLED_ALPHA = 0.38f

// 355 dp without padding: the control's width inside a product card on a 411 dp screen.
@Preview(name = "Light", widthDp = 379)
@Preview(name = "Dark", widthDp = 379, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartQuantityControlPreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CartQuantityControl(
                    quantity = 0,
                    onAdd = {},
                    onIncrease = {},
                    onDecrease = {},
                    onRemoveAll = {},
                )
                CartQuantityControl(
                    quantity = 1,
                    onAdd = {},
                    onIncrease = {},
                    onDecrease = {},
                    onRemoveAll = {},
                )
                CartQuantityControl(
                    quantity = 3,
                    onAdd = {},
                    onIncrease = {},
                    onDecrease = {},
                    onRemoveAll = {},
                    canIncrease = false,
                )
                ShimmerPlaceholder {
                    CartQuantityControlPlaceholder()
                }
            }
        }
    }
}
