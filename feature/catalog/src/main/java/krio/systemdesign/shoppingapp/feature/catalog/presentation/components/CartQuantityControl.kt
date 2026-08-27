package krio.systemdesign.shoppingapp.feature.catalog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CartQuantityControl(
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemoveAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controlHeight = ButtonDefaults.MinHeight
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    if (quantity <= 0) {
        FilledTonalButton(
            onClick = onAdd,
            modifier = modifier
                .fillMaxWidth()
                .height(controlHeight),
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("В корзину")
        }
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(controlHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Surface(
            shape = RoundedCornerShape(controlHeight / 2),
            color = containerColor,
            contentColor = contentColor,
            modifier = Modifier.height(controlHeight),
        ) {
            Row(
                modifier = Modifier.height(controlHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CartControlIconButton(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Уменьшить количество",
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
                    contentDescription = "Добавить ещё",
                    onClick = onIncrease,
                    size = controlHeight,
                    contentColor = contentColor,
                )
            }
        }
        CartControlIconButton(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Удалить из корзины",
            onClick = onRemoveAll,
            size = controlHeight,
            containerColor = containerColor,
            contentColor = contentColor,
        )
    }
}

@Composable
private fun CartControlIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = contentColor,
        )
    }
}
