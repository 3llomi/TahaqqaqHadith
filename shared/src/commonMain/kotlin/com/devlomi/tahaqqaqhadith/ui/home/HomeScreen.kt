package com.devlomi.tahaqqaqhadith.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalUriHandler
import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity
import com.devlomi.tahaqqaqhadith.BASE_URL
import com.devlomi.tahaqqaqhadith.common.util.Util
import com.devlomi.tahaqqaqhadith.data.HadithQueryPlaceholderDataSource
import com.devlomi.tahaqqaqhadith.data.model.HadithEntry
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyAssessment
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyState
import com.devlomi.tahaqqaqhadith.ui.theme.ErrorBg
import com.devlomi.tahaqqaqhadith.ui.theme.HadithTheme
import io.ktor.http.encodeURLParameter
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_book
import myapplication.shared.generated.resources.ic_book_2
import myapplication.shared.generated.resources.ic_close
import myapplication.shared.generated.resources.ic_edu
import myapplication.shared.generated.resources.ic_no_search_results
import myapplication.shared.generated.resources.ic_person
import myapplication.shared.generated.resources.ic_question_mark
import myapplication.shared.generated.resources.ic_verify
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@Composable
fun HomeScreen(state: HomeState, onEvent: (HomeEvents) -> Unit) {
    var expandedHadithKeys by remember { mutableStateOf(setOf<String>()) }
    val uriHandler = LocalUriHandler.current

    //TODO DATA SHOULD BE FILTERED AND GROUPED FROM VM?
    val groupedEntries = remember(state.data?.entries) {
        state.data.orEmptyGroupedEntries()
    }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
//        onEvent(HomeEvents.OnQueryTextChange("بورك"))
//        onEvent(HomeEvents.Search)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
//            AppToolbar()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    bottom = 16.dp,
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    //TODO IMPROVE THIS ANIMATION, AND MAKE SURE SEARCH INPUT IS NOT TIED TO HERO SECTION
                    if (state.data == null ) {
                        AnimatedVisibility(
                            visible = isVisible,
                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        ) {
                            HeroSearchSection(
                                query = state.query,
                                placeholder = state.queryPlaceholder,
                                onQueryChange = { onEvent(HomeEvents.OnQueryTextChange(it)) },
                                onSearch = {
                                    isVisible = false
                                    onEvent(HomeEvents.Search)
                                }
                            )
                        }

                    } else {
                        SearchInputBar(
                            query = state.query,
                            placeholder = state.queryPlaceholder,
                            onQueryChange = { onEvent(HomeEvents.OnQueryTextChange(it)) },
                            onSearch = { onEvent(HomeEvents.Search) }
                        )
                    }
                }

                when {
                    state.isLoading -> {
                        item {
                            ShimmerLoadingList()
                        }
                    }

                    state.data != null -> {
                        if (groupedEntries.isEmpty()) {
                            item {
                                NoResultsHeader()
                            }
                        } else {
                            items(groupedEntries, key = { it.key }) { group ->
                                HadithGroupCard(
                                    group = group,
                                    isExpanded = group.key in expandedHadithKeys,
                                    onToggle = {
                                        expandedHadithKeys = if (group.key in expandedHadithKeys) {
                                            expandedHadithKeys - group.key
                                        } else {
                                            expandedHadithKeys + group.key
                                        }
                                    }
                                )
                            }
                        }
                    }

                    state.fakeHadith != null -> {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            FakeHadithCard(
                                state.fakeHadith!!,
                                onCorrectHadithClick = {
                                    val fakeHadith = state.fakeHadith ?: return@FakeHadithCard
                                    val url = fakeHadith.correctHadithUrl ?: return@FakeHadithCard
                                    uriHandler.openUri(url)
                                }, onVerifySourceClick = {
                                    val fakeHadith = state.fakeHadith ?: return@FakeHadithCard
                                    val url = "${BASE_URL}fake-hadith/${
                                        fakeHadith.id.toString().encodeURLParameter()
                                    }"
                                    uriHandler.openUri(url)
                                })
                        }
                    }

                }
                item {
                    // Show More Button
                    if (state.data?.entries?.isNotEmpty() == true && state.submittedSearchQuery.isNotEmpty()) {
                        ShowMoreResultsButton(
                            query = state.submittedSearchQuery,
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            }
                        )
                    }
                }
            }

            Text(
                "جميع الأحاديث مُقدمة من موقع الدرر السُنية ولا نملك أي حقوق للمحتوى.",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.height(8.dp))


        }
    }
}

@Composable
private fun AppToolbar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "تحقق الحديث",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun HeroSearchSection(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_book_2),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Text(
                text = "تحقق من صحة الحديث",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "اكتشف صحة الأحاديث النبوية من خلال قواعد بيانات موثقة ومحققة من قبل كبار العلماء والباحثين.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            //TODO WHY WE HAVE TWO SEARCH BARS? MAYBE REMOVE ONE
            SearchInputBar(
                query = query,
                placeholder = placeholder,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
            )
        }
    }
}

@Composable
private fun SearchInputBar(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        //TODO Consider USING THIS AS ROOT
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "بحث",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    maxLines = 3,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Right,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (query.isBlank()) {
                            Text(
                                text = placeholder,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.36f),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}


@Composable
private fun HadithGroupCard(
    group: HadithGroup,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    var showScoreDialog by remember { mutableStateOf(false) }
    val groupStateColor = stateColor(group.bestEntry.assessment.state)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.25.dp, groupStateColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 280,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(modifier = Modifier.width(12.dp))
                            StatusPill(
                                text = group.bestEntry.assessment.state.toUiTitle(),
                                state = group.bestEntry.assessment.state
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${group.narrations.size} روايات",
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            ScoreCircle(
                                score = group.bestEntry.assessment.score,
                                state = group.bestEntry.assessment.state,
                                onClick = { showScoreDialog = true }
                            )
                        }
                        Text(
                            text = "\"${group.bestEntry.hadithText}\"",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {


                MetaRow(
                    label = "الراوي",
                    value = group.bestEntry.narrator,
                    iconRes = Res.drawable.ic_person,
                )
                MetaRow(
                    label = "المحدث",
                    value = group.bestEntry.scholar,
                    iconRes = Res.drawable.ic_edu,
                )
                MetaRow(
                    label = "المصدر",
                    value = "${group.bestEntry.source} (${group.bestEntry.pageOrNumber})",
                    iconRes = Res.drawable.ic_book,
                )
                MetaRow(
                    label = "الحُكم",
                    value = group.bestEntry.verdict,
                    iconRes = stateIcon(group.bestEntry.assessment.state),
                    valueColor = stateColor(group.bestEntry.assessment.state),
                    iconTintColor = stateColor(group.bestEntry.assessment.state),
                    iconTintBgColor = if (group.bestEntry.assessment.state == LegitimacyState.WEAK_OR_REJECTED) ErrorBg
                    else MaterialTheme.colorScheme.tertiary.copy(
                        alpha = 0.18f
                    ),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggle)
                ) {
                    val iconRotation by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 250),
                        label = "expand_icon_rotation"
                    )
                    Text(
                        text = "التفاصيل",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = if (isExpanded) "إخفاء الروايات" else "عرض الروايات",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.graphicsLayer { rotationZ = iconRotation }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(animationSpec = tween(280)) + fadeIn(
                        animationSpec = tween(
                            220
                        )
                    ),
                    exit = shrinkVertically(animationSpec = tween(220)) + fadeOut(
                        animationSpec = tween(
                            180
                        )
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                        group.narrations.forEachIndexed { index, entry ->
                            NarrationCard(entry = entry, index = index + 1)
                        }
                    }
                }
            }
        }
    }

    if (showScoreDialog) {
        ScoreExplanationDialog(
            score = group.bestEntry.assessment.score,
            assessment = group.bestEntry.assessment,
            onDismiss = { showScoreDialog = false }
        )
    }
}

@Composable
private fun ScoreCircle(
    score: Int,
    state: LegitimacyState,
    onClick: () -> Unit
) {
    val color = stateColor(state)
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 3.dp,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScoreExplanationDialog(
    score: Int,
    assessment: LegitimacyAssessment,
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "شرح الدرجة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = stateColor(assessment.state),
                                strokeWidth = 2.5.dp,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                            Text(
                                text = score.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = stateColor(assessment.state),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الدرجة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "كيف يتم إحتساب الدرجة؟",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = ".يتم إحتسابها بناءً على عدد الروايات الموجودة وصحة كل رواية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Justify
                    )



                    Text(
                        text = "ملاحظة",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "هذه الدرجة لا تعني صحة الحديث بشكل قطعي، بل هي مؤشر على صحة الحديث بناءً على الروايات المتاحة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("حسناً")
                }
            }
        )
    }
}

@Composable
private fun ScoreCriteriaItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatusPill(text: String, state: LegitimacyState) {
    val (background, content) = when (state) {
        LegitimacyState.AUTHENTIC -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        LegitimacyState.NEEDS_REVIEW -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        LegitimacyState.WEAK_OR_REJECTED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
private fun MetaRow(
    label: String,
    value: String,
    iconRes: DrawableResource,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTintColor: Color = MaterialTheme.colorScheme.onTertiary,
    iconTintBgColor: Color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(iconTintBgColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    tint = iconTintColor,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = valueColor,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
            }


        }
    }
}

@Composable
private fun NarrationCard(entry: HadithEntry, index: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NarrationDetailLine(
                label = "رواية",
                value = "#$index",
                iconRes = Res.drawable.ic_book,
            )

            NarrationDetailLine(
                label = "الراوي",
                value = entry.narrator,
                iconRes = Res.drawable.ic_person,
            )
            NarrationDetailLine(
                label = "المحدث",
                value = entry.scholar,
                iconRes = Res.drawable.ic_edu,
            )
            NarrationDetailLine(
                label = "المصدر",
                value = "${entry.source} (${entry.pageOrNumber})",
                iconRes = Res.drawable.ic_book,
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            NarrationDetailLine(
                label = "الحُكم",
                value = entry.verdict,
                iconRes = stateIcon(entry.assessment.state),
                valueColor = stateColor(entry.assessment.state),
                iconTintColor = stateColor(entry.assessment.state)
            )

        }
    }
}

@Composable
private fun NarrationDetailLine(
    label: String,
    value: String,
    iconRes: DrawableResource,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTintColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTintColor,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            textAlign = TextAlign.Right,
            modifier = Modifier.width(66.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            textAlign = TextAlign.Right,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

private data class HadithGroup(
    val key: String,
    val bestEntry: HadithEntry,
    val narrations: List<HadithEntry>,
)

private fun HadithSearchResult?.orEmptyGroupedEntries(): List<HadithGroup> {
    if (this == null) return emptyList()

    return entries
        .groupBy { Util.sanitizeHadithText(it.hadithText) }
        .mapNotNull { (key, narrations) ->
            val sorted = narrations.sortedByDescending { it.assessment.score }
            val best = sorted.firstOrNull() ?: return@mapNotNull null
            HadithGroup(key = key, bestEntry = best, narrations = sorted)
        }
        .sortedByDescending { it.bestEntry.assessment.score }
}

@Composable
private fun FakeHadithCard(
    fakeHadith: FakeHadith_Entity,
    onCorrectHadithClick: () -> Unit,
    onVerifySourceClick: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            //TODO USE THEME COLOR INSTEAD
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5EC)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
                Text(
                    text = "حديث مكذوب منتشر",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.45f),
                )
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError
                        )
                        Text(
                            text = "مكذوب",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = fakeHadith.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                if (fakeHadith.grade != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الدرجة:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            text = fakeHadith.grade,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )

                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                if (fakeHadith.correctHadithUrl != null) {
                    Button(
                        onClick = onCorrectHadithClick,
                        colors = ButtonDefaults.buttonColors(
                            //TODO MOVE COLOR TO COLORS
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.CheckCircleOutline, contentDescription = null,
//                            tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "عرض الحديث الصحيح",
                                style = MaterialTheme.typography.labelLarge,
//                            color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }


                OutlinedButton(
                    onClick = onVerifySourceClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        //TODO MOVE COLOR TO COLORS
                        contentColor = Color(0xFFF9F5EC)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Folder, contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "التحقق من المصدر",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun FakeHadithCardPreview() {
    HadithTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            FakeHadithCard(
                fakeHadith = FakeHadith_Entity(
                    id = 1,
                    text = "أحاديثُ في فَضلِ الإسكَندريَّةِ وعَسقلانَ، وأنَّ المُقيمَ بها ثلاثةَ أيَّامٍ مِن غيرِ رياءٍ كمَن عَبَد اللهَ سبعينَ سَنةً.",
                    page = 123,
                    correctHadithUrl = "https://dorar.net/hadith/12345",
                    grade = "ضعيف",
                    timestamp = 0,
                    seen = 0,
                ), onCorrectHadithClick = {

                },
                onVerifySourceClick = {}
            )
        }
    }
}

@Composable
private fun ShimmerLoadingList() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            ShimmerGroupCard()
        }
    }
}

@Composable
private fun ShimmerGroupCard() {
    val shimmerBrush = rememberShimmerBrush()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(shimmerBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush)
            )
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(shimmerBrush)
                )
            }
        }
    }
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val xShift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    val base = MaterialTheme.colorScheme.surface
    return Brush.linearGradient(
        colors = listOf(
            base.copy(alpha = 0.65f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            base.copy(alpha = 0.65f)
        ),
        start = Offset(xShift.value - 220f, 0f),
        end = Offset(xShift.value, 220f)
    )
}

@Composable
private fun stateColor(state: LegitimacyState): Color {
    return when (state) {
        LegitimacyState.AUTHENTIC -> MaterialTheme.colorScheme.secondary
        LegitimacyState.NEEDS_REVIEW -> MaterialTheme.colorScheme.primary
        LegitimacyState.WEAK_OR_REJECTED -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun stateIcon(state: LegitimacyState): DrawableResource {
    return when (state) {
        LegitimacyState.AUTHENTIC -> Res.drawable.ic_verify
        LegitimacyState.NEEDS_REVIEW -> Res.drawable.ic_question_mark
        LegitimacyState.WEAK_OR_REJECTED -> Res.drawable.ic_close
    }
}

private fun LegitimacyState.toUiTitle(): String {
    return when (this) {
        LegitimacyState.AUTHENTIC -> "راجح الصحة"
        LegitimacyState.NEEDS_REVIEW -> "يحتاج مراجعة"
        LegitimacyState.WEAK_OR_REJECTED -> "ضعيف أو غير ثابت"
    }
}

@Composable
private fun ShowMoreResultsButton(
    query: String,
    onOpenUrl: (String) -> Unit
) {
    //TODO USE HTTP ENCODE?
    val encodedQuery = query.trim().encodeURLParameter()

    val dorarUrl = "https://dorar.net/hadith/search?q=$encodedQuery"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenUrl(dorarUrl) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عرض المزيد من النتائج",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Launch,
                    contentDescription = "الانتقال إلى الموقع الخارجي",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun HadithGroupCardPreviewCollapsed() {
    HadithTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            HadithGroupCard(
                group = previewGroup(),
                isExpanded = false,
                onToggle = {}
            )
        }
    }
}

@Preview
@Composable
private fun HadithGroupCardPreviewExpanded() {
    HadithTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            HadithGroupCard(
                group = previewGroup(),
                isExpanded = true,
                onToggle = {}
            )
        }
    }
}

private fun previewGroup(): HadithGroup {
    val topAssessment = LegitimacyAssessment(
        score = 88,
        state = LegitimacyState.AUTHENTIC,
        reason = "تعدد الطرق وتحسن بعض الأسانيد"
    )
    val secondaryAssessment = LegitimacyAssessment(
        score = 64,
        state = LegitimacyState.NEEDS_REVIEW,
        reason = "في الإسناد اختلاف يحتاج مراجعة"
    )

    val topEntry = HadithEntry(
        index = 1,
        hadithText = "إنما الأعمال بالنيات، وإنما لكل امرئ ما نوى، فمن كانت هجرته إلى الله ورسوله فهجرته إلى الله ورسوله.",
        narrator = "عمر بن الخطاب",
        scholar = "البخاري",
        source = "صحيح البخاري",
        pageOrNumber = "1",
        verdict = "أصل هذا الحديث في إسناده إنما هو عن ابن عجلان عن ربيعة بن عثمان عن محمد بن يحيى بن حبان عن الأعرج",
        assessment = topAssessment
    )

    val secondEntry = HadithEntry(
        index = 2,
        hadithText = topEntry.hadithText,
        narrator = "عائشة أم المؤمنين",
        scholar = "الترمذي",
        source = "سنن الترمذي",
        pageOrNumber = "1647",
        verdict = "حسن",
        assessment = secondaryAssessment
    )

    return HadithGroup(
        key = Util.sanitizeHadithText(topEntry.hadithText),
        bestEntry = topEntry,
        narrations = listOf(topEntry, secondEntry)
    )
}


@Composable
@Preview
private fun NoResultsHeader() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(114.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(Res.drawable.ic_no_search_results),
                modifier = Modifier.padding(8.dp),
                contentDescription = null,
            )
        }


        Text(
            "لم يتم العثور على نتائج",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "قد يكون الحديث الذي تبحث عنه غير موجود أو غير صحيح",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}