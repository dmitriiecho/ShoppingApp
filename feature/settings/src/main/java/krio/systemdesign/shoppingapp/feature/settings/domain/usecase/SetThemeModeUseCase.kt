package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.domain.repository.AppSettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    suspend operator fun invoke(mode: ThemeMode): Result<Unit> = appSettingsRepository.setThemeMode(mode)
}
