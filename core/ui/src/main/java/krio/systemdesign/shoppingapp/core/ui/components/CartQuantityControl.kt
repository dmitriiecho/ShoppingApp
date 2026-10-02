package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R

@Composable
fun CartQuantityControl(
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemoveAll: () -> Unit,
    modifier: Modifier = Modifier,
    // false — «+» выключен: в корзине уже весь доступный остаток или товар закончился. «−» и удаление работают.
    canIncrease: Boolean = true,
) {
    val controlHeight = ButtonDefaults.MinHeight
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    if (quantity <= 0) {
        WithoutTouchTargetReserve {
            // Залитая кнопка акцентного цвета, как и переключатель количества, который появляется после добавления.
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

    // Высота не меньше controlHeight, а не ровно она: с крупным шрифтом в настройках телефона число растёт,
    // и переключатель растёт вместе с ним, а не обрезает его.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = controlHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    modifier = Modifier.widthIn(min = 24.dp),
                    textAlign = TextAlign.Center,
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

// Заглушка CartQuantityControl внутри ShimmerPlaceholder: фигура того же размера и формы, что кнопка «В корзину».
@Composable
fun CartQuantityControlPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonDefaults.MinHeight)
            .shimmerShape(RoundedCornerShape(percent = 50)),
    )
}

// Кнопки корзины рисуются высотой 40 dp и места занимают столько же. Обычно кнопка Material занимает 48 dp
// с запасом под палец, и карточка товара от этого стала бы выше. Нажимать без запаса не труднее:
// Compose сам расширяет зону нажатия элементов меньше 48 dp, если рядом нет других нажимаемых элементов.
@Composable
internal fun WithoutTouchTargetReserve(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp, content = content)
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
            // Выключенная кнопка всё равно забирает нажатие себе. Иначе оно уходит к тому, что под ней:
            // нажатие на серый «+» открывало бы карточку товара, на которой лежат кнопки.
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

// Прозрачность выключенных элементов в Material 3.
private const val DISABLED_ALPHA = 0.38f
