package krio.systemdesign.shoppingapp.core.ui.components.images

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.core.ui.animation.LocalSharedTransitionScope
import krio.systemdesign.shoppingapp.core.ui.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

// Картинка товара. У картинок с сервера прозрачный фон, поэтому под ней своя плитка:
// светлое пятно в центре и чуть тонированные края, как на студийной фотографии.
// Товар вписывается целиком, с отступом от краёв плитки.
// Пока картинка грузится, по плитке бежит блик (шиммер). Если загрузить не удалось,
// на плитке остаётся значок «картинки нет»: рамка с пейзажем, перечёркнутая наискосок.
// Черта — акцентный цвет темы. В тёмной теме он светлее, поэтому значок другой.
// Цвета плитки — ShoppingAppTheme.colors.productImageCenter и productImageEdge.
@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = ShapeRadius.Medium,
    // У самих картинок поля уже есть, поэтому в маленькой плитке отступ почти не нужен.
    contentPadding: Dp = 2.dp,
    // Плитки с одним ключом на двух экранах — один общий элемент: при переходе между экранами
    // плитка перелетает с места на одном экране на место на другом (см. LocalSharedTransitionScope).
    sharedElementKey: Any? = null,
) {
    val colors = ShoppingAppTheme.colors
    var isLoading by remember { mutableStateOf(false) }
    val shared = sharedElement(sharedElementKey, cornerRadius)
    val placeholder = painterResource(
        if (colors.isDark) R.drawable.product_image_placeholder_dark else R.drawable.product_image_placeholder,
    )
    val context = LocalPlatformContext.current
    val request = remember(context, imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            // Пока грузится картинка нужного размера, показываем ту, что уже лежит в памяти, если есть.
            // Например, на карточке товара сразу видна картинка из списка, иначе она прилетела бы пустой плиткой.
            .placeholderMemoryCacheKey(imageUrl)
            .build()
    }
    Box(
        modifier = modifier
            .then(shared.tile)
            .clip(RoundedCornerShape(shared.cornerRadius))
            .background(Brush.radialGradient(listOf(colors.productImageCenter, colors.productImageEdge))),
    ) {
        if (isLoading) {
            Shimmer(
                base = colors.productImageEdge,
                highlight = colors.productImageCenter,
                modifier = Modifier.matchParentSize(),
            )
        }
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .then(shared.image),
            contentScale = ContentScale.Fit,
            // error и onState у AsyncImage в разных перегрузках, поэтому заглушку подставляем сами:
            // у состояния ошибки появляется картинка, и Coil рисует её так же, как успешную.
            transform = { state ->
                if (state is AsyncImagePainter.State.Error) state.copy(painter = placeholder) else state
            },
            // Если картинка из памяти уже видна, шиммер не нужен.
            onState = { isLoading = it is AsyncImagePainter.State.Loading && it.painter == null },
        )
    }
}

// tile и image — модификаторы плитки и картинки на ней, cornerRadius — скругление углов плитки сейчас.
private class SharedElement(val tile: Modifier, val image: Modifier, val cornerRadius: Dp)

// Плитка и картинка на ней летят как два общих элемента, каждый к своему месту.
// Если бы картинка летела внутри плитки, в начале полёта её отступ сразу стал бы как у плитки, куда она летит:
// из списка (отступ 2 dp) на карточку (32 dp) картинка сначала резко уменьшилась бы, а потом начала расти.
// Без ключа или на экране без анимации перехода (LocalNavAnimatedVisibilityScope) плитка никуда не летит.
@Composable
private fun sharedElement(key: Any?, cornerRadius: Dp): SharedElement {
    // Скругление плитки, из которой эта вылетает, если такая плитка сейчас на экране.
    // Запоминается при первом появлении: потом в tileCornerRadii уже эта плитка.
    val startCornerRadius = remember(key) { key?.let { tileCornerRadii[it]?.value } }
    if (key != null) {
        DisposableEffect(key, cornerRadius) {
            val entry = TileCornerRadius(cornerRadius)
            tileCornerRadii[key] = entry
            onDispose {
                // Плитка на новом экране могла уже записать себя под тем же ключом.
                if (tileCornerRadii[key] === entry) tileCornerRadii.remove(key)
            }
        }
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    if (key == null || sharedTransitionScope == null || animatedVisibilityScope == null) {
        return SharedElement(tile = Modifier, image = Modifier, cornerRadius = cornerRadius)
    }
    // Пока плитка летит, скругление плавно меняется от скругления плитки, из которой она вылетела, к своему:
    // из списка (12 dp) на карточку (без скругления) и обратно углы не меняются рывком.
    // Пружина та же, что у полёта по умолчанию, поэтому углы меняются в такт с размером.
    val animatedCornerRadius by animatedVisibilityScope.transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
        label = "productImageCornerRadius",
    ) { state ->
        if (state == EnterExitState.PreEnter && startCornerRadius != null) startCornerRadius else cornerRadius
    }
    return with(sharedTransitionScope) {
        SharedElement(
            // Летит одна плитка — та, что на экране, куда переходим.
            // Если бы старая и новая плитки плавно сменяли друг друга, посередине полёта обе были бы
            // полупрозрачными и сквозь них просвечивал бы список.
            tile = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(SharedTileKey(key)),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            image = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(SharedImageKey(key)),
                animatedVisibilityScope = animatedVisibilityScope,
                // Над летящей плиткой, а не под ней.
                zIndexInOverlay = 1f,
            ),
            cornerRadius = animatedCornerRadius,
        )
    }
}

// Скругление углов плиток с ключом общего элемента, которые сейчас на экране, по ключу.
// Плитка на новом экране узнаёт отсюда скругление плитки, из которой вылетает.
private val tileCornerRadii = mutableMapOf<Any, TileCornerRadius>()

// Отдельный объект на каждую плитку: при удалении плитка убирает из tileCornerRadii только себя.
private class TileCornerRadius(val value: Dp)

private data class SharedTileKey(val key: Any)

private data class SharedImageKey(val key: Any)

// Светлая полоса шириной с плитку, которая раз за разом проходит по ней слева направо.
// В начале и в конце прохода полоса целиком за краем, поэтому новый проход начинается без рывка.
@Composable
private fun Shimmer(
    base: Color,
    highlight: Color,
    modifier: Modifier = Modifier,
) {
    val progress = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmerProgress",
    )
    // progress читается только при рисовании: каждый кадр анимации перерисовывает плитку без перекомпоновки.
    Spacer(
        modifier = modifier.drawBehind {
            val bandWidth = size.width
            val bandStart = -bandWidth + progress.value * (size.width + bandWidth)
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(base, highlight, base),
                    startX = bandStart,
                    endX = bandStart + bandWidth,
                ),
            )
        },
    )
}
