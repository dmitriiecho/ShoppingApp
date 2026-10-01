package krio.systemdesign.shoppingapp.core.ui.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

// Общий элемент — один и тот же элемент на двух экранах (например, картинка товара в списке и на карточке товара).
// Пока один экран сменяет другой, такой элемент перелетает со своего места на первом экране на место на втором.

// Слой, поверх которого летят общие элементы. Его задаёт AppNavHost вокруг NavHost.
// Null — сейчас ничего не перелетает (например, пока одна вкладка сменяет другую).
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// Анимация перехода к экрану и от него. Её задаёт экран в графе навигации, если его общие элементы должны перелетать.
// На экранах без неё элементы не перелетают, а появляются и исчезают вместе с экраном.
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }
