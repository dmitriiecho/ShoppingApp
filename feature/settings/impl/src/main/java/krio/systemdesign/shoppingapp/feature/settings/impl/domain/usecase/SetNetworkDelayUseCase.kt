package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

class SetNetworkDelayUseCase @Inject constructor(private val appSettingsRepository: AppSettingsRepository) {
    suspend operator fun invoke(delay: NetworkDelay): Result<Unit> = appSettingsRepository.setNetworkDelay(delay)
}
