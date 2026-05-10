package com.anhnn.language

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    onLanguageSaved: (langCode: String) -> Unit = {}
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.anhnn_select_language),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                }

                val canConfirm = selectedLanguageCode != null && selectedLanguageCode != currentLanguageCode
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
fun LanguageItem(
    language: LanguageManager.Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

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
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
        }
    }
}
