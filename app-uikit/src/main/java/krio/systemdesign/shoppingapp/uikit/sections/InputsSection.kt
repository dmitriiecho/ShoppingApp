package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.AppTextField
import krio.systemdesign.shoppingapp.core.ui.components.SearchField
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup

@Composable
fun InputsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        // AppTextField рассчитан на карточку, поэтому и в примере он внутри неё.
        sampleGroup("AppTextField") {
            SampleVariant(stringResource(R.string.uikit_variant_inside_card)) {
                var street by rememberSaveable { mutableStateOf("") }
                var comment by rememberSaveable { mutableStateOf("") }
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppTextField(
                            value = street,
                            onValueChange = { street = it },
                            label = stringResource(R.string.uikit_sample_street),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = stringResource(R.string.uikit_sample_courier_comment),
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_disabled)) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    AppTextField(
                        value = "",
                        onValueChange = {},
                        label = stringResource(R.string.uikit_sample_street),
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
        sampleGroup("SearchField") {
            SampleVariant {
                var query by rememberSaveable { mutableStateOf("") }
                SearchField(
                    query = query,
                    onQueryChange = { query = it },
                    onClear = { query = "" },
                    placeholder = stringResource(R.string.uikit_sample_search_products),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
