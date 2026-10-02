package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkDelayUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    operator fun invoke(): Flow<NetworkDelay> = appSettingsRepository.observeNetworkDelay()
}
