package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

class SetThemeModeUseCase @Inject constructor(private val appSettingsRepository: AppSettingsRepository) {
    suspend operator fun invoke(mode: ThemeMode): Result<Unit> = appSettingsRepository.setThemeMode(mode)
}
