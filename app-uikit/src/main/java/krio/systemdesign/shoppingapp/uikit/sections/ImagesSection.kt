package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.images.ProductImage
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun ImagesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("ProductImage") {
            SampleVariant(stringResource(R.string.uikit_variant_loaded)) {
                ProductImage(
                    imageUrl = SampleData.headphonesImageUrl,
                    contentDescription = stringResource(R.string.uikit_sample_product_headphones),
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_load_failed)) {
                ProductImage(
                    imageUrl = SampleData.missingImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
        }
    }
}

private val SAMPLE_IMAGE_SIZE = 120.dp
