package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.dto.LocateTarget
import com.v2ray.ang.dto.entities.ProfileItem
import com.v2ray.ang.dto.entities.SubscriptionUsageInfo
import com.v2ray.ang.ui.compose.ItemDivider
import com.v2ray.ang.ui.compose.LocalPvpnThemeStyle
import com.v2ray.ang.ui.compose.colorConfigType
import com.v2ray.ang.ui.compose.colorPing
import com.v2ray.ang.ui.compose.colorPingRed
import com.v2ray.ang.ui.compose.verticalScrollbar
import kotlin.math.abs

@Composable
fun GroupPagerPage(
    groupId: String,
    mainViewModel: MainViewModel,
    selectedGuid: String?,
    locateTarget: LocateTarget?,
    doubleColumnDisplay: Boolean,
    searchQuery: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onMoreServer: (String, ProfileItem) -> Unit,
    onRemoveServer: (String, String) -> Unit,
    usageInfo: SubscriptionUsageInfo?,
    groupTitle: String,
    onOpenLink: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val groupStateFlow = remember(groupId) { mainViewModel.serverGroupState(groupId) }
    val groupState by groupStateFlow.collectAsStateWithLifecycle()
    val actions = remember(onSelectServer, onEditServer, onMoreServer, onRemoveServer) {
        ServerRowActions(onSelectServer, onEditServer, onMoreServer, onRemoveServer)
    }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    var infoHeightPx by remember { mutableIntStateOf(0) }
    val listPadding = if (usageInfo == null) contentPadding else PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        top = contentPadding.calculateTopPadding() + with(density) { infoHeightPx.toDp() },
        end = contentPadding.calculateEndPadding(layoutDirection),
        bottom = contentPadding.calculateBottomPadding()
    )

    Box(Modifier.fillMaxSize()) {
        ServerListPage(
            rows = groupState.rows,
            selectedGuid = selectedGuid,
            locateTarget = locateTarget?.takeIf { it.groupId == groupId },
            doubleColumnDisplay = doubleColumnDisplay,
            groupId = groupId,
            lazyListStates = lazyListStates,
            lazyGridStates = lazyGridStates,
            actions = actions,
            onLocateHandled = { mainViewModel.onAction(MainAction.LocateHandled) },
            contentPadding = listPadding
        )
        if (usageInfo != null) {
            SubscriptionInfoCard(
                info = usageInfo,
                fallbackTitle = groupTitle,
                onOpenLink = onOpenLink,
                onRefresh = { mainViewModel.onAction(MainAction.UpdateGroupSubscription(groupId)) },
                modifier = Modifier.align(Alignment.TopCenter)
                    .onSizeChanged { infoHeightPx = it.height }
            )
        }
    }
}

private class ServerRowActions(
    val select: (String) -> Unit,
    val edit: (String, ProfileItem) -> Unit,
    val more: (String, ProfileItem) -> Unit,
    val remove: (String, String) -> Unit
)

private data class ServerCountry(
    val id: String,
    val title: String,
    val flag: String,
    val order: Int
)

private data class ServerCountryGroup(
    val country: ServerCountry,
    val rows: List<ServerRowUiModel>
)

private val knownServerCountries = listOf(
    ServerCountry("de", "آلمان", "🇩🇪", 1),
    ServerCountry("us", "آمریکا", "🇺🇸", 2),
    ServerCountry("nl", "هلند", "🇳🇱", 3),
    ServerCountry("fr", "فرانسه", "🇫🇷", 4),
    ServerCountry("gb", "بریتانیا", "🇬🇧", 5),
    ServerCountry("tr", "ترکیه", "🇹🇷", 6),
    ServerCountry("fi", "فنلاند", "🇫🇮", 7),
    ServerCountry("se", "سوئد", "🇸🇪", 8),
    ServerCountry("ca", "کانادا", "🇨🇦", 9),
    ServerCountry("ru", "روسیه", "🇷🇺", 10),
    ServerCountry("sg", "سنگاپور", "🇸🇬", 11),
    ServerCountry("jp", "ژاپن", "🇯🇵", 12),
    ServerCountry("pl", "لهستان", "🇵🇱", 13),
    ServerCountry("ch", "سوئیس", "🇨🇭", 14),
    ServerCountry("at", "اتریش", "🇦🇹", 15),
    ServerCountry("ae", "امارات", "🇦🇪", 16),
    ServerCountry("es", "اسپانیا", "🇪🇸", 17),
    ServerCountry("it", "ایتالیا", "🇮🇹", 18),
    ServerCountry("no", "نروژ", "🇳🇴", 19),
    ServerCountry("be", "بلژیک", "🇧🇪", 20),
    ServerCountry("other", "سایر سرورها", "🌐", 999)
)

private fun detectServerCountry(remarks: String): ServerCountry {
    val value = remarks.lowercase()
    fun hasCode(code: String): Boolean =
        Regex("(^|[^a-z])${Regex.escape(code)}([^a-z]|$)", RegexOption.IGNORE_CASE)
            .containsMatchIn(remarks)

    val match = when {
        "🇩🇪" in remarks || "germany" in value || "deutschland" in value || "آلمان" in value || hasCode("DE") -> "de"
        "🇺🇸" in remarks || "united states" in value || "america" in value || "آمریکا" in value || hasCode("USA") || hasCode("US") -> "us"
        "🇳🇱" in remarks || "netherlands" in value || "holland" in value || "هلند" in value || hasCode("NL") -> "nl"
        "🇫🇷" in remarks || "france" in value || "فرانسه" in value || hasCode("FR") -> "fr"
        "🇬🇧" in remarks || "united kingdom" in value || "great britain" in value || "england" in value || "بریتانیا" in value || hasCode("GB") || hasCode("UK") -> "gb"
        "🇹🇷" in remarks || "turkey" in value || "türkiye" in value || "ترکیه" in value || hasCode("TR") -> "tr"
        "🇫🇮" in remarks || "finland" in value || "فنلاند" in value || hasCode("FI") -> "fi"
        "🇸🇪" in remarks || "sweden" in value || "سوئد" in value || hasCode("SE") -> "se"
        "🇨🇦" in remarks || "canada" in value || "کانادا" in value || hasCode("CA") -> "ca"
        "🇷🇺" in remarks || "russia" in value || "روسیه" in value || hasCode("RU") -> "ru"
        "🇸🇬" in remarks || "singapore" in value || "سنگاپور" in value || hasCode("SG") -> "sg"
        "🇯🇵" in remarks || "japan" in value || "ژاپن" in value || hasCode("JP") -> "jp"
        "🇵🇱" in remarks || "poland" in value || "لهستان" in value || hasCode("PL") -> "pl"
        "🇨🇭" in remarks || "switzerland" in value || "سوئیس" in value || hasCode("CH") -> "ch"
        "🇦🇹" in remarks || "austria" in value || "اتریش" in value || hasCode("AT") -> "at"
        "🇦🇪" in remarks || "uae" in value || "united arab emirates" in value || "امارات" in value || hasCode("AE") -> "ae"
        "🇪🇸" in remarks || "spain" in value || "اسپانیا" in value || hasCode("ES") -> "es"
        "🇮🇹" in remarks || "italy" in value || "ایتالیا" in value || hasCode("IT") -> "it"
        "🇳🇴" in remarks || "norway" in value || "نروژ" in value || hasCode("NO") -> "no"
        "🇧🇪" in remarks || "belgium" in value || "بلژیک" in value || hasCode("BE") -> "be"
        else -> "other"
    }
    return knownServerCountries.first { it.id == match }
}

private fun buildCountryGroups(rows: List<ServerRowUiModel>): List<ServerCountryGroup> {
    return rows.groupBy { detectServerCountry(it.remarks) }
        .map { (country, countryRows) -> ServerCountryGroup(country, countryRows) }
        .sortedBy { it.country.order }
}

@Composable
private fun CountrySectionHeader(country: ServerCountry, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 9.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 13.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(country.flag, fontSize = 19.sp)
        Text(
            country.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Box(
            modifier = Modifier.clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f))
                .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ServerListPage(
    rows: List<ServerRowUiModel>,
    selectedGuid: String?,
    locateTarget: LocateTarget?,
    doubleColumnDisplay: Boolean,
    groupId: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    actions: ServerRowActions,
    onLocateHandled: () -> Unit,
    contentPadding: PaddingValues
) {
    val countryGroups = remember(rows) { buildCountryGroups(rows) }

    if (doubleColumnDisplay) {
        val gridState = remember(groupId) { lazyGridStates.getOrPut(groupId) { LazyGridState() } }
        LocateTargetEffect(locateTarget, countryGroups, gridState, onLocateHandled)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize().verticalScrollbar(gridState),
            contentPadding = contentPadding
        ) {
            countryGroups.forEach { countryGroup ->
                item(
                    key = "country_header_${countryGroup.country.id}",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    CountrySectionHeader(countryGroup.country, countryGroup.rows.size)
                }
                itemsIndexed(
                    items = countryGroup.rows,
                    key = { _, item -> item.guid }
                ) { _, row ->
                    ServerItemColumn(row, row.guid == selectedGuid, true, actions)
                }
            }
        }
    } else {
        val listState = remember(groupId) { lazyListStates.getOrPut(groupId) { LazyListState() } }
        LocateTargetEffect(locateTarget, countryGroups, listState, onLocateHandled)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().verticalScrollbar(listState),
            contentPadding = contentPadding
        ) {
            countryGroups.forEach { countryGroup ->
                item(key = "country_header_${countryGroup.country.id}") {
                    CountrySectionHeader(countryGroup.country, countryGroup.rows.size)
                }
                itemsIndexed(
                    items = countryGroup.rows,
                    key = { _, item -> item.guid }
                ) { _, row ->
                    ServerItemRow(row, row.guid == selectedGuid, actions)
                    ItemDivider()
                }
            }
        }
    }
}

@Composable
private fun LocateTargetEffect(
    target: LocateTarget?,
    countryGroups: List<ServerCountryGroup>,
    state: LazyListState,
    onHandled: () -> Unit
) {
    if (target == null) return
    LaunchedEffect(target, countryGroups) {
        var lazyIndex = 0
        for (group in countryGroups) {
            val rowIndex = group.rows.indexOfFirst { it.guid == target.serverGuid }
            if (rowIndex >= 0) {
                state.scrollToItem(lazyIndex + 1 + rowIndex, -state.layoutInfo.viewportSize.height / 3)
                onHandled()
                return@LaunchedEffect
            }
            lazyIndex += 1 + group.rows.size
        }
    }
}

@Composable
private fun LocateTargetEffect(
    target: LocateTarget?,
    countryGroups: List<ServerCountryGroup>,
    state: LazyGridState,
    onHandled: () -> Unit
) {
    if (target == null) return
    LaunchedEffect(target, countryGroups) {
        var lazyIndex = 0
        for (group in countryGroups) {
            val rowIndex = group.rows.indexOfFirst { it.guid == target.serverGuid }
            if (rowIndex >= 0) {
                state.scrollToItem(lazyIndex + 1 + rowIndex, -state.layoutInfo.viewportSize.height / 3)
                onHandled()
                return@LaunchedEffect
            }
            lazyIndex += 1 + group.rows.size
        }
    }
}

@Composable
private fun ServerItemRow(
    row: ServerRowUiModel,
    isSelected: Boolean,
    actions: ServerRowActions
) {
    ServerListItem(row, isSelected, false, actions)
}

@Composable
private fun ServerItemColumn(
    row: ServerRowUiModel,
    isSelected: Boolean,
    doubleColumnDisplay: Boolean,
    actions: ServerRowActions
) {
    Column {
        ServerListItem(row, isSelected, doubleColumnDisplay, actions)
        ItemDivider()
    }
}

@Composable
private fun ServerListItem(
    row: ServerRowUiModel,
    isSelected: Boolean,
    doubleColumnDisplay: Boolean,
    actions: ServerRowActions
) {
    val testResult = if (row.testDelayMillis == 0L) "—"
        else stringResource(R.string.server_test_delay_value, row.testDelayMillis)
    val selectedDescription = if (isSelected) stringResource(R.string.acc_selected_server) else null
    val themeStyle = LocalPvpnThemeStyle.current
    val accent = MaterialTheme.colorScheme.primary
    val cardShape = RoundedCornerShape(themeStyle.cardRadius)
    val innerShape = RoundedCornerShape((themeStyle.cardRadius.value * 0.62f).dp)
    val pingColor = if (row.testDelayMillis < 0L) colorPingRed else colorPing
    val borderColor = when {
        isSelected -> accent.copy(alpha = 0.92f)
        themeStyle.useGlow -> accent.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
    }
    val cardBackground = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = themeStyle.cardGlassAlpha)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 3.dp)
            .then(
                if (themeStyle.cardElevation > 0.dp) Modifier.shadow(
                    elevation = themeStyle.cardElevation + if (isSelected) 2.dp else 0.dp,
                    shape = cardShape,
                    ambientColor = accent.copy(alpha = if (themeStyle.useGlow) 0.20f else 0.05f),
                    spotColor = accent.copy(alpha = if (themeStyle.useGlow) 0.16f else 0.04f)
                ) else Modifier
            )
            .clip(cardShape)
            .background(cardBackground)
            .border(
                width = if (isSelected) themeStyle.cardBorderWidth + 0.5.dp else themeStyle.cardBorderWidth,
                color = borderColor,
                shape = cardShape
            )
            .semantics { if (selectedDescription != null) stateDescription = selectedDescription }
            .clickable { actions.select(row.guid) }
    ) {
        Box(
            Modifier.fillMaxWidth().height(if (isSelected) 3.dp else 1.dp)
                .background(if (isSelected) accent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.24f))
        )
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.size(10.dp).clip(CircleShape)
                        .background(if (isSelected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
                )
                Text(
                    row.remarks,
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        lineBreak = LineBreak.Paragraph
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (isSelected) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(accent.copy(alpha = 0.14f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            stringResource(R.string.acc_selected_server),
                            color = accent,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
                if (doubleColumnDisplay) {
                    IconButton(
                        onClick = { actions.more(row.guid, row.profile) },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_more_vert_24dp),
                            stringResource(R.string.acc_more),
                            Modifier.size(21.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    IconButton(
                        onClick = { actions.more(row.guid, row.profile) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_more_vert_24dp),
                            stringResource(R.string.acc_more),
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { actions.edit(row.guid, row.profile) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_edit_24dp),
                            stringResource(R.string.acc_edit),
                            Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { actions.remove(row.guid, row.remarks) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_delete_24dp),
                            stringResource(R.string.acc_delete),
                            Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.88f)
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().clip(innerShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSelected) 0.48f else 0.34f))
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (row.subscriptionBadge.isNotBlank()) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.16f))
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                    ) {
                        Text(
                            row.subscriptionBadge.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                            maxLines = 1
                        )
                    }
                }
                Text(
                    row.statistics,
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.62f))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        row.typeDescription,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorConfigType,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(pingColor.copy(alpha = 0.12f))
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(pingColor))
                    Text(
                        testResult,
                        style = MaterialTheme.typography.labelSmall,
                        color = pingColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

internal suspend fun PagerState.navigateToPageOptimized(
    targetPage: Int,
    animateAdjacentPage: Boolean = true
) {
    if (pageCount <= 0) return
    val target = targetPage.coerceIn(0, pageCount - 1)
    val current = settledPage.coerceIn(0, pageCount - 1)
    if (target == current) return
    if (abs(target - current) == 1 && animateAdjacentPage) {
        animateScrollToPage(target)
    } else {
        scrollToPage(target)
    }
}
