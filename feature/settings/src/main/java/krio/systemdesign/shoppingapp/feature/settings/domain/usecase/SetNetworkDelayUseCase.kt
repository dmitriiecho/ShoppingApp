package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.repository.AppSettingsRepository
import javax.inject.Inject

class SetNetworkDelayUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    suspend operator fun invoke(delay: NetworkDelay): Result<Unit> = appSettingsRepository.setNetworkDelay(delay)
}
