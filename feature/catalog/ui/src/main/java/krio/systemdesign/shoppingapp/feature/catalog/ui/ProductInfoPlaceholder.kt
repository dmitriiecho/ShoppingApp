package krio.systemdesign.shoppingapp.feature.catalog.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.shimmerShape

@Composable
fun ProductInfoPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
                .shimmerShape(MaterialTheme.shapes.extraSmall),
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(28.dp)
                .shimmerShape(MaterialTheme.shapes.extraSmall),
        )
        Spacer(Modifier.height(16.dp))
        listOf(1f, 1f, 0.6f).forEach { widthFraction ->
            Box(
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .fillMaxWidth(widthFraction)
                    .height(18.dp)
                    .shimmerShape(MaterialTheme.shapes.extraSmall),
            )
        }
    }
}
