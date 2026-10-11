package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

@Inject
internal class ObserveNetworkDelayUseCase(private val appSettingsRepository: AppSettingsRepository) {
    operator fun invoke(): Flow<NetworkDelay> = appSettingsRepository.observeNetworkDelay()
}
