package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RewardItem
import com.example.ui.RiseViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHighlight
import com.example.ui.theme.PurpleRoyalty
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsShopScreen(
    viewModel: RiseViewModel,
    onNavigateBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    var showCreateRewardDialog by remember { mutableStateOf(false) }

    val userCoins = userProfile?.coins ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rewards Shop",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("shop_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateRewardDialog = true },
                containerColor = GoldPrimary,
                contentColor = NavyDarkest,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("create_custom_reward_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Custom Reward")
            }
        },
        containerColor = NavyBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Coins Balance Header Banner
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(22.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AVAILABLE COIN BALANCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$userCoins",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = GoldBright
                                )
                            }
                            Text(
                                text = "Spend coins on real-life treats you define",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSilver
                            )
                        }

                        Button(
                            onClick = { showCreateRewardDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavySurfaceElevated,
                                contentColor = GoldPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Custom", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Real-Life Rewards Vault (${rewards.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            items(rewards, key = { it.id }) { reward ->
                val canAfford = userCoins >= reward.coinPrice

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (canAfford) GoldPrimary.copy(alpha = 0.5f) else NavySurfaceBorder,
                            RoundedCornerShape(18.dp)
                        )
                        .testTag("reward_item_${reward.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceElevated)
                                    .border(1.dp, GoldSecondary.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Text(text = reward.iconEmoji, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = reward.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                if (reward.description.isNotBlank()) {
                                    Text(
                                        text = reward.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSilver
                                    )
                                }
                                if (reward.timesClaimed > 0) {
                                    Text(
                                        text = "Enjoyed ${reward.timesClaimed} ${if (reward.timesClaimed == 1) "time" else "times"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Button(
                                onClick = { viewModel.redeemReward(reward) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = NavyDarkest,
                                    disabledContainerColor = NavySurfaceElevated,
                                    disabledContentColor = TextMuted
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("redeem_button_${reward.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${reward.coinPrice} 🪙",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            if (!canAfford) {
                                Text(
                                    text = "Need ${reward.coinPrice - userCoins} more",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            } else if (!reward.isPreset) {
                                IconButton(
                                    onClick = { viewModel.deleteReward(reward) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextMuted.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateRewardDialog) {
            CreateRewardDialog(
                onDismiss = { showCreateRewardDialog = false },
                onConfirm = { title, description, price, emoji ->
                    viewModel.addCustomReward(title, description, price, emoji)
                    showCreateRewardDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateRewardDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, price: Int, emoji: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coinPrice by remember { mutableIntStateOf(300) }
    var selectedEmoji by remember { mutableStateOf("🎁") }

    val emojiPresets = listOf("🎁", "🎬", "🍕", "🎮", "📚", "☕", "🍔", "👟", "🏖️", "🎧", "🍦", "🛌")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Create Real-Life Reward 🎁",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldPrimary
                )
                Text(
                    text = "Treat yourself when you achieve goals! Set something you genuinely look forward to.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Emoji selector
                Text("Select Reward Icon:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojiPresets) { emoji ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedEmoji == emoji) GoldPrimary.copy(alpha = 0.3f) else NavySurfaceElevated)
                                .border(
                                    if (selectedEmoji == emoji) 2.dp else 0.dp,
                                    GoldPrimary,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedEmoji = emoji }
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reward Name (e.g. Sushi Dinner)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = NavySurfaceElevated,
                        unfocusedContainerColor = NavySurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reward_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Details (Optional)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = NavySurfaceElevated,
                        unfocusedContainerColor = NavySurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Coin Price: $coinPrice 🪙", style = MaterialTheme.typography.labelMedium, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(150, 250, 400, 600).forEach { price ->
                        Surface(
                            onClick = { coinPrice = price },
                            shape = RoundedCornerShape(10.dp),
                            color = if (coinPrice == price) GoldPrimary else NavySurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$price 🪙",
                                color = if (coinPrice == price) NavyDarkest else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavySurfaceElevated,
                            contentColor = TextSilver
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description, coinPrice, selectedEmoji)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDarkest
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("create_reward_confirm_button")
                    ) {
                        Text("Save Reward 🎁", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
