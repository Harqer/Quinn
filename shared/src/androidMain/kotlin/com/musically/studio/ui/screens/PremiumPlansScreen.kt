package com.musically.studio.ui.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.molecules.PlanCard
import com.musically.studio.ui.theme.*

// ---------------------------------------------------------------------------
// Data model — private to this file, driving PlanCard instances
// ---------------------------------------------------------------------------

private data class SubscriptionTier(
    val productId: String,
    val name: String,
    val price: String,
    val billingPeriod: String = "/ mo",
    val features: List<String>,
    val badge: String? = null,
    val isHighlighted: Boolean = false,
)

private val SUBSCRIPTION_TIERS = listOf(
    SubscriptionTier(
        productId = "premium_basic",
        name = "Basic Creator",
        price = "$20",
        features = listOf(
            "30 AI-generated songs",
            "60 min Real-time sessions",
            "50 Cover images",
            "10 Music videos",
        ),
    ),
    SubscriptionTier(
        productId = "premium_pro",
        name = "Pro Studio",
        price = "$50",
        features = listOf(
            "100 AI-generated songs",
            "150 min Real-time sessions",
            "200 Cover images",
            "40 Music videos",
            "Commercial use license",
            "Priority generation queue",
        ),
        badge = "MOST POPULAR",
        isHighlighted = true,
    ),
    SubscriptionTier(
        productId = "premium_ultra",
        name = "Ultra Unlimited",
        price = "$100",
        features = listOf(
            "Unlimited AI-generated songs",
            "Unlimited Real-time sessions",
            "Unlimited Cover images & videos",
            "Commercial use license",
            "Highest priority queue",
            "Dedicated support channel",
        ),
    ),
)

private data class FaqItem(val question: String, val answer: String)

private val FAQ_ITEMS = listOf(
    FaqItem(
        question = "Can I cancel anytime?",
        answer = "Yes — cancel from your Google Play subscriptions page or through this app at any time. You keep access until the end of your current billing period."
    ),
    FaqItem(
        question = "What is Real-time session time?",
        answer = "Real-time sessions let Mave generate music live while it processes your camera or voice. Your monthly allocation resets on your billing date."
    ),
    FaqItem(
        question = "Does commercial use apply to all tiers?",
        answer = "Commercial use is included in Pro Studio and Ultra Unlimited plans. Basic Creator songs are for personal use only."
    ),
    FaqItem(
        question = "What happens if I downgrade?",
        answer = "Your existing songs are always yours. Downgrading affects future generation limits, not content you've already created."
    ),
)

private data class FeatureHighlight(val icon: ImageVector, val title: String, val description: String)

private val FEATURE_HIGHLIGHTS = listOf(
    FeatureHighlight(
        icon = Icons.Default.MusicNote,
        title = "Lyria 3 & Magenta RT",
        description = "Google's most advanced music generation models, available exclusively on Mave."
    ),
    FeatureHighlight(
        icon = Icons.Default.VideoLibrary,
        title = "AI Cover Art & Video",
        description = "Generate professional cover images and music videos directly from your sessions."
    ),
    FeatureHighlight(
        icon = Icons.Default.Star,
        title = "Real-time with Glasses",
        description = "Stream AI music live from Meta Ray-Ban glasses — the world's first ambient music experience."
    ),
    FeatureHighlight(
        icon = Icons.Default.Lock,
        title = "Commercial License",
        description = "Pro and Ultra plans include a commercial use license. Publish and monetize your tracks freely."
    ),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

/**
 * Dedicated full-screen destination for Mave subscription plans.
 *
 * Reads [viewModel.isPremium] and [viewModel.userSettings] to determine which
 * tier (if any) the user currently holds, then renders a scrollable grid of
 * [PlanCard]s, a feature-highlight section, an FAQ accordion, and a legal footer.
 *
 * All billing flows are delegated to [viewModel.launchBillingFlow] — this screen
 * owns no billing logic directly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPlansScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val currentProductId by viewModel.currentProductId.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaveBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mave Premium",
                        color = MaveOnSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaveOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaveBackground,
                    scrolledContainerColor = MaveSurface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = MaveSpacing().medium,
                end = MaveSpacing().medium,
                top = MaveSpacing().large,
                bottom = 120.dp // clearance for mini-player
            ),
            verticalArrangement = Arrangement.spacedBy(MaveSpacing().medium),
            horizontalArrangement = Arrangement.spacedBy(MaveSpacing().medium)
        ) {

            // ----------------------------------------------------------------
            // Hero Header — full width across all column counts
            // ----------------------------------------------------------------
            item(span = { GridItemSpan(maxLineSpan) }) {
                PremiumHeroHeader(isPremium = isPremium)
            }

            // ----------------------------------------------------------------
            // Section label
            // ----------------------------------------------------------------
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Choose Your Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaveOnSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { heading() }
                )
            }

            // ----------------------------------------------------------------
            // Plan cards — adaptive: 1 col on phone, 2+ on tablet/desktop
            // ----------------------------------------------------------------
            items(SUBSCRIPTION_TIERS) { tier ->
                PlanCard(
                    planName = tier.name,
                    price = tier.price,
                    billingPeriod = tier.billingPeriod,
                    features = tier.features,
                    badge = tier.badge,
                    isHighlighted = tier.isHighlighted,
                    isCurrentPlan = isPremium && tier.productId == currentProductId,
                    isLoading = isLoading,
                    onSelectClick = {
                        activity?.let { viewModel.launchBillingFlow(it, tier.productId) }
                    }
                )
            }

            // ----------------------------------------------------------------
            // What you unlock — feature highlights
            // ----------------------------------------------------------------
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(MaveSpacing().large))
                Text(
                    text = "What You Unlock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaveOnSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { heading() }
                )
                Spacer(modifier = Modifier.height(MaveSpacing().medium))
            }

            items(FEATURE_HIGHLIGHTS) { highlight ->
                FeatureHighlightRow(highlight = highlight)
            }

            // ----------------------------------------------------------------
            // FAQ accordion — full width
            // ----------------------------------------------------------------
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(MaveSpacing().large))
                Text(
                    text = "Common Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaveOnSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { heading() }
                )
                Spacer(modifier = Modifier.height(MaveSpacing().small))
            }

            items(FAQ_ITEMS) { faq ->
                FaqAccordionItem(faq = faq)
            }

            // ----------------------------------------------------------------
            // Legal footer
            // ----------------------------------------------------------------
            item(span = { GridItemSpan(maxLineSpan) }) {
                PremiumFooter(
                    isPremium = isPremium,
                    onManageSubscription = { viewModel.launchStripePortal() }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composables — private, focused, < 60 lines each
// ---------------------------------------------------------------------------

@Composable
private fun PremiumHeroHeader(isPremium: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Gradient wordmark
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaveOnSurface)) { append("Mave ") }
                withStyle(SpanStyle(color = MaveBrand)) { append("Premium") }
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )

        Text(
            text = "Unlock the full power of AI music creation.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaveOnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isPremium) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaveBrand.copy(alpha = 0.15f),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaveBrand,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "You're a Premium member",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaveBrand
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun FeatureHighlightRow(highlight: FeatureHighlight) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaveSurfaceContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaveBrand.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = highlight.icon,
                contentDescription = null,
                tint = MaveBrand,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = highlight.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaveOnSurface
            )
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaveOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun FaqAccordionItem(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }
    val iconTint by animateColorAsState(
        targetValue = if (expanded) MaveBrand else MaveOnSurfaceVariant,
        label = "faq_icon_tint"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaveSurfaceContainer
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaveOnSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (expanded) {
                HorizontalDivider(
                    color = MaveOnSurfaceVariant.copy(alpha = 0.12f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaveOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumFooter(
    isPremium: Boolean,
    onManageSubscription: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaveSpacing().large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isPremium) {
            TextButton(onClick = onManageSubscription) {
                Text(
                    text = "Manage Subscription",
                    color = MaveBrand,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Restore Purchase",
            style = MaterialTheme.typography.bodySmall,
            color = MaveOnSurfaceVariant,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO: hook into billing client restorePurchases */ }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Subscriptions auto-renew unless cancelled at least 24 hours before the period ends. " +
                    "Manage or cancel anytime via Google Play.",
            style = MaterialTheme.typography.labelSmall,
            color = MaveOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun PremiumHeroHeaderFreePreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            PremiumHeroHeader(isPremium = false)
        }
    }
}

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun PremiumHeroHeaderPaidPreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            PremiumHeroHeader(isPremium = true)
        }
    }
}

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun FeatureHighlightPreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            FeatureHighlightRow(
                highlight = FeatureHighlight(
                    icon = Icons.Default.MusicNote,
                    title = "Lyria 3 & Magenta RT",
                    description = "Google's most advanced music generation models."
                )
            )
        }
    }
}

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun FaqAccordionPreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            FaqAccordionItem(
                faq = FaqItem(
                    question = "Can I cancel anytime?",
                    answer = "Yes — cancel from your Google Play subscriptions page at any time."
                )
            )
        }
    }
}
