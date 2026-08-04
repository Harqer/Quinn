package com.musically.studio.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.molecules.FaqAccordionItem
import com.musically.studio.ui.components.molecules.FeatureHighlightRow
import com.musically.studio.ui.components.molecules.PlanCard
import com.musically.studio.ui.components.organisms.PremiumFooter
import com.musically.studio.ui.components.organisms.PremiumHeroHeader
import com.musically.studio.ui.theme.*

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

    var dynamicSubscriptionTiers by remember { mutableStateOf(com.musically.studio.ui.screens.SUBSCRIPTION_TIERS) }
    var dynamicFaqItems by remember { mutableStateOf(com.musically.studio.ui.screens.FAQ_ITEMS) }

    LaunchedEffect(Unit) {
        try {
            val plansResult = com.musically.studio.dataconnect.DefaultConnector.instance.listSubscriptionPlans().execute()
            if (plansResult.data.subscriptionPlans.isNotEmpty()) {
                dynamicSubscriptionTiers = plansResult.data.subscriptionPlans.map { plan ->
                    com.musically.studio.ui.screens.SubscriptionTier(
                        productId = plan.id,
                        name = plan.name,
                        price = "$${plan.priceMonthly.toInt()}",
                        features = plan.features.map { it.feature },
                        badge = if (plan.tier.value.name == "PRO") "MOST POPULAR" else null,
                        isHighlighted = plan.tier.value.name == "PRO"
                    )
                }.sortedBy { it.price.replace("$", "").toIntOrNull() ?: 0 }
            }
            
            val faqResult = com.musically.studio.dataconnect.DefaultConnector.instance.listFaqItems().execute()
            if (faqResult.data.faqItems.isNotEmpty()) {
                dynamicFaqItems = faqResult.data.faqItems.map { faq ->
                    com.musically.studio.ui.screens.FaqItem(
                        question = faq.question,
                        answer = faq.answer
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("PremiumPlansScreen", "Failed to load DataConnect", e)
        }
    }

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
            item(span = { GridItemSpan(maxLineSpan) }) {
                PremiumHeroHeader(isPremium = isPremium)
            }

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

            items(dynamicSubscriptionTiers) { tier ->
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

            items(dynamicFaqItems) { faq ->
                FaqAccordionItem(faq = faq)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                PremiumFooter(
                    isPremium = isPremium,
                    onManageSubscription = { viewModel.launchStripePortal() },
                    onRestorePurchases = { viewModel.restorePurchases() }
                )
            }
        }
    }
}
