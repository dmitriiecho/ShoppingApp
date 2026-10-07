package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

internal class ObserveNetworkDelayUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    operator fun invoke(): Flow<NetworkDelay> = appSettingsRepository.observeNetworkDelay()
}
