package krio.systemdesign.shoppingapp.shared.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

class ObserveThemeModeUseCase @Inject constructor(private val appSettingsRepository: AppSettingsRepository) {
    operator fun invoke(): Flow<ThemeMode> = appSettingsRepository.observeThemeMode()
}
