package krio.systemdesign.shoppingapp.shared.domain.usecase

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

@Inject
class ObserveThemeModeUseCase(private val appSettingsRepository: AppSettingsRepository) {
    operator fun invoke(): Flow<ThemeMode> = appSettingsRepository.observeThemeMode()
}
