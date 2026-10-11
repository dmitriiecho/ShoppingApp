package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

@Inject
internal class SetThemeModeUseCase(private val appSettingsRepository: AppSettingsRepository) {
    suspend operator fun invoke(mode: ThemeMode): Result<Unit> = appSettingsRepository.setThemeMode(mode)
}
