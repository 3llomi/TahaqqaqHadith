package com.devlomi.tahaqqaqhadith.ui.walkthrough

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.devlomi.tahaqqaqhadith.ui.theme.HadithTheme
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_wlk_false
import myapplication.shared.generated.resources.ic_wlk_search
import myapplication.shared.generated.resources.ic_wlk_veri
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class WalkthroughPage(
    val id: Int,
    val title: String,
    val description: String,
    val details: String,
    val icon: DrawableResource,
    val iconTint: Color? = null
)

@Composable
fun WalkthroughScreen(onCompleted: () -> Unit = {}) {
    val pages = listOf(
        WalkthroughPage(
            id = 1,
            title = "إبحث بسهولة",
            description = "إبحث عن أي حديث بسرعة وبدقة من مصادر موثوقة",
            details = "",
            icon = Res.drawable.ic_wlk_search,
        ),
        WalkthroughPage(
            id = 2,
            title = "تأكد من الصحة",
            description = "إعرف درجة صحة الأحاديث من خلال آراء المختصين والعلماء",
            details = "",
            icon = Res.drawable.ic_wlk_veri,
        ),
        WalkthroughPage(
            id = 3,
            title = "اكتشف الأحاديث الغير صحيحة",
            description = "تعرف على الأحاديث المشهورة التي لا تصح",
            details = "",
            icon = Res.drawable.ic_wlk_false,
            iconTint = MaterialTheme.colorScheme.error
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "تحقق - حديث",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
                TextButton(
                    modifier = Modifier.align(
                        Alignment.CenterEnd
                    ), onClick = {
                        onCompleted.invoke()
                    }) {
                    Text(
                        "تخطي",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Pager Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 0,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    WalkthroughPageContent(page)
                }
            }


            // Progress text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "${pagerState.currentPage + 1} من ${pages.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress Indicator - Dots and active pill with animation
                Row(
                    modifier = Modifier.height(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isActive = index == pagerState.currentPage

                        // Animate width from 6dp (dot) to 24dp (pill)
                        val animatedWidth by animateDpAsState(
                            targetValue = if (isActive) 24.dp else 6.dp,
                            animationSpec = tween(durationMillis = 300),
                            label = "stepper_width_$index"
                        )

                        // Animate color
                        val animatedColor by animateColorAsState(
                            targetValue = if (isActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            animationSpec = tween(durationMillis = 300),
                            label = "stepper_color_$index"
                        )

                        Box(
                            modifier = Modifier
                                .width(animatedWidth)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(animatedColor)
                        )
                    }
                }


                Spacer(modifier = Modifier.weight(1f))

                // Previous Button - Dark circular
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(
                            enabled = pagerState.currentPage > 0,
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Next Button - Simple arrow
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable(
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        onCompleted.invoke()
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (pagerState.currentPage < pages.size - 1) Icons.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardDoubleArrowLeft,
                        tint = MaterialTheme.colorScheme.background,
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WalkthroughPageContent(page: WalkthroughPage) {
    var authenticHadithProgress by remember { mutableStateOf(0f) }
    var incorrectHadithProgress by remember { mutableStateOf(0f) }
    val progressAnimationDuration = 1000
    val authenticProgressAnimation by animateFloatAsState(
        targetValue = authenticHadithProgress,
        animationSpec = tween(
            durationMillis = progressAnimationDuration,
            easing = FastOutLinearInEasing
        )
    )

    //TODO: FIX ANIMATION - NOT ALWAYS WORKS - PROBABLY PAGER IS SAVING STATE
    val incorrectProgressAnimation by animateFloatAsState(
        targetValue = incorrectHadithProgress,
        animationSpec = tween(
            durationMillis = progressAnimationDuration,
            easing = FastOutLinearInEasing
        )
    )
    var iconVisible by remember { mutableStateOf(false) }


    LaunchedEffect(page.id) {
        iconVisible = true

        if (page.id == 2) {
            authenticHadithProgress = 1f
        }
        if (page.id == 3) {
            incorrectHadithProgress = 1f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Circle
        AnimatedVisibility(iconVisible, enter = scaleIn(initialScale = 0.5f) + fadeIn()) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(page.icon),
                    contentDescription = null,
                    tint = page.iconTint ?: MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(78.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        Text(
            text = page.details,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        if (page.id == 2) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            modifier = Modifier.size(32.dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    Column {
                        Row {
                            Text(
                                "درجة الصحة: صحيح",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "المصدر: البخاري",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }

                        LinearProgressIndicator(
                            progress = {
                                authenticProgressAnimation
                            },
                            color = MaterialTheme.colorScheme.onTertiary,

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }


                }

            }
        }
        if (page.id == 3) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            modifier = Modifier.size(32.dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                    Column {
                        Text(
                            "درجة الصحة: لا أصل له",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        LinearProgressIndicator(
                            progress = {
                                incorrectProgressAnimation
                            },
                            color = MaterialTheme.colorScheme.error,

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }


                }

            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun WalkthroughScreenPreview() {
    HadithTheme {
        WalkthroughScreen()
    }
}
