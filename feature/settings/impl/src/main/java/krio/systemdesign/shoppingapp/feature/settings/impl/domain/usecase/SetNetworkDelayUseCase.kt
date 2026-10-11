package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

@Inject
internal class SetNetworkDelayUseCase(private val appSettingsRepository: AppSettingsRepository) {
    suspend operator fun invoke(delay: NetworkDelay): Result<Unit> = appSettingsRepository.setNetworkDelay(delay)
}
