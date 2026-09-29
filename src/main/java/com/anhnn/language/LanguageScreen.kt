package com.anhnn.language

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Màn hình chọn ngôn ngữ dùng chung.
 *
 * @param onBack       Gọi khi người dùng nhấn nút Back.
 * @param onLanguageSaved Gọi sau khi lưu thành công, trả về langCode mới.
 *                     App thường gọi recreate() hoặc restart ở đây.
 */
@Composable
private fun LockPortrait() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity ?: return@DisposableEffect onDispose {}
        val original = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { activity.requestedOrientation = original }
    }
}

/**
 * Màn chọn ngôn ngữ app.
 *
 * [isFirstSetup] bật khi đây là lần chọn ngôn ngữ ĐẦU TIÊN (sau splash, trước khi vào app).
 * Lúc đó nút xác nhận phải bấm được ngay cả khi người dùng giữ nguyên ngôn ngữ đang chọn sẵn
 * — nếu không, ai muốn dùng đúng ngôn ngữ mặc định sẽ không có đường nào đi tiếp. Ở màn đổi
 * ngôn ngữ trong Cài đặt thì để mặc định `false`: chưa đổi gì thì không có gì để lưu.
 */
@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    onLanguageSaved: (langCode: String) -> Unit = {},
    isFirstSetup: Boolean = false,
) {
    LockPortrait()
    val context = LocalContext.current
    val languageDataSource = remember { LanguageDataSource(context) }
    val scope = rememberCoroutineScope()

    var currentLanguageCode by remember { mutableStateOf<String?>(null) }
    var selectedLanguageCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentLanguageCode = languageDataSource.languageCode.first()
        selectedLanguageCode = currentLanguageCode
    }

    val colorScheme = MaterialTheme.colorScheme
    val languages = LanguageManager.getSupportedLanguages()

    val confirmSelection: () -> Unit = {
        selectedLanguageCode?.let { code ->
            scope.launch {
                languageDataSource.setLanguageCode(code)
                currentLanguageCode = code
                LanguageManager.setLanguage(context, code)
                onLanguageSaved(code)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.background, colorScheme.surfaceVariant)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.anhnn_select_language),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val canConfirm = selectedLanguageCode != null &&
                    (isFirstSetup || selectedLanguageCode != currentLanguageCode)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageSwapButton(
                        currentLang = selectedLanguageCode ?: currentLanguageCode ?: languages.first().code,
                        onToggle = {
                            val activeCode = selectedLanguageCode ?: currentLanguageCode
                            val activeIndex = languages.indexOfFirst { it.code == activeCode }
                            val nextIndex = if (activeIndex == -1) 0 else (activeIndex + 1) % languages.size
                            selectedLanguageCode = languages[nextIndex].code
                        }
                    )
                    IconButton(
                        onClick = confirmSelection,
                        enabled = canConfirm,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (canConfirm) colorScheme.primary
                            else colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Confirm",
                            tint = if (canConfirm) colorScheme.onPrimary
                            else colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(languages) { language ->
                    LanguageItem(
                        language = language,
                        isSelected = selectedLanguageCode == language.code,
                        onClick = { selectedLanguageCode = language.code }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSwapButton(
    currentLang: String,
    onToggle: () -> Unit
) {
    FilledTonalButton(
        onClick = onToggle,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        AnimatedContent(
            targetState = currentLang.uppercase(Locale.ROOT),
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> -height } + fadeOut()
                )
            },
            label = "lang_swap_anim"
        ) { targetLang ->
            Text(
                text = targetLang,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LanguageItem(
    language: LanguageManager.Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "language_item_scale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primaryContainer
        else colorScheme.surface.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 300),
        label = "colorAnim"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) androidx.compose.ui.graphics.Color.Transparent
        else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 300),
        label = "borderAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (!isSelected) BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = language.flagResId),
                    contentDescription = "${language.displayName} flag",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = language.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}
