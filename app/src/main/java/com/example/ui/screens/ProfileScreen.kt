package com.example.ui.screens

import android.app.TimePickerDialog
import android.speech.tts.Voice
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.ui.RiseViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: RiseViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val availableVoices by viewModel.ttsManager.availableVoices.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryItem?>(null) }

    val level = userProfile?.level ?: 1
    val currentXp = userProfile?.xp ?: 0
    val (minXp, maxXp) = when (level) {
        1 -> Pair(0, 150)
        2 -> Pair(150, 350)
        3 -> Pair(350, 700)
        4 -> Pair(700, 1200)
        5 -> Pair(1200, 2000)
        6 -> Pair(2000, 3000)
        7 -> Pair(3000, 4500)
        else -> Pair(4500, 6000)
    }
    val levelProgress = ((currentXp - minXp).toFloat() / (maxXp - minXp).coerceAtLeast(1)).coerceIn(0f, 1f)

    var speedSlider by remember(userProfile?.speechSpeed) { mutableFloatStateOf(userProfile?.speechSpeed ?: 0.95f) }
    var pitchSlider by remember(userProfile?.speechPitch) { mutableFloatStateOf(userProfile?.speechPitch ?: 0.85f) }
    var selectedVoiceName by remember(userProfile?.speechVoiceName) { mutableStateOf(userProfile?.speechVoiceName ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("profile_back_button")
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
            // 1. HERO USER LEVEL CARD
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary)
                                ) {
                                    Text(
                                        text = "${userProfile?.userName?.firstOrNull() ?: 'E'}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NavyDarkest
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile?.userName ?: "Emmanuel",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = TextWhite
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { showEditNameDialog = true },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Name",
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = GoldPrimary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Level $level • ${userProfile?.levelTitle ?: "Starter"}",
                                            color = GoldBright,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$currentXp XP",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = GoldBright
                                )
                                Text(
                                    text = "${maxXp - currentXp} XP to next rank",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldPrimary,
                            trackColor = NavySurfaceElevated,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            // 2. STREAKS & HABIT FORMATION
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${userProfile?.currentStreak ?: 1} Day Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow
                                )
                            }
                            Text(
                                text = "To-Do List Streak: ${userProfile?.listCompletionStreak ?: 0} days",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSilver
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NavySurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (userProfile?.streakShieldAvailable == true) "Shield Ready 🛡️" else "Used (Recharges Mon)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. TEXT-TO-SPEECH VOICE PICKER & TUNING (REQUIREMENT 2)
            item {
                Text(
                    text = "Text-to-Speech Voice Studio 🎙️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Speech Speed: ${String.format("%.2f", speedSlider)}x",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSilver
                        )
                        Slider(
                            value = speedSlider,
                            onValueChange = {
                                speedSlider = it
                                viewModel.updateSpeechSettings(speedSlider, pitchSlider, selectedVoiceName)
                            },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = GoldPrimary,
                                activeTrackColor = GoldPrimary,
                                inactiveTrackColor = NavySurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Voice Pitch (Default: Deep adult voice 0.85): ${String.format("%.2f", pitchSlider)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSilver
                        )
                        Slider(
                            value = pitchSlider,
                            onValueChange = {
                                pitchSlider = it
                                viewModel.updateSpeechSettings(speedSlider, pitchSlider, selectedVoiceName)
                            },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = FlameOrange,
                                activeTrackColor = FlameOrange,
                                inactiveTrackColor = NavySurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Voice Picker list
                        Text(
                            text = "Phone TTS Voices (${availableVoices.size} detected):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (availableVoices.isEmpty()) {
                            Text(
                                text = "Using default system English voice (deep adult configuration).",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(availableVoices.take(12)) { voice ->
                                    val isSelected = selectedVoiceName == voice.name
                                    Surface(
                                        onClick = {
                                            selectedVoiceName = voice.name
                                            viewModel.updateSpeechSettings(speedSlider, pitchSlider, voice.name)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) GoldPrimary else NavySurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldPrimary else NavySurfaceBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = voice.name.substringAfter("voice_").take(14),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) NavyDarkest else TextWhite
                                            )
                                            Text(
                                                text = voice.locale.language.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = if (isSelected) NavyDarkest.copy(alpha = 0.7f) else TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.previewVoice(selectedVoiceName, speedSlider, pitchSlider) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Preview Spoken Voice", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. TASK CATEGORIES MANAGER (REQUIREMENT 4)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Categories (${categories.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Surface(
                        onClick = { showAddCategoryDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceElevated
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category", style = MaterialTheme.typography.labelSmall, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(categories, key = { it.id }) { cat ->
                val catColor = try {
                    Color(android.graphics.Color.parseColor(cat.colorHex))
                } catch (e: Exception) {
                    GoldPrimary
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(catColor.copy(alpha = 0.2f))
                            ) {
                                Text(cat.iconEmoji, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                if (cat.description.isNotBlank()) {
                                    Text(
                                        text = cat.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSilver,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Row {
                            IconButton(
                                onClick = { editingCategory = cat },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                            if (!cat.isDefault) {
                                IconButton(
                                    onClick = { viewModel.deleteCategory(cat) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 5. ALARM & SUNDAY SETTINGS
            item {
                Text(
                    text = "Alarm & Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val alarmHour = userProfile?.alarmHour ?: 7
                        val alarmMin = userProfile?.alarmMinute ?: 0
                        val alarmEnabled = userProfile?.alarmEnabled ?: true

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Morning Wake-Up Alarm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Loud ALARM stream, rings until 'I'm up'", style = MaterialTheme.typography.bodySmall, color = TextSilver)
                            }

                            Switch(
                                checked = alarmEnabled,
                                onCheckedChange = { viewModel.updateAlarmTime(alarmHour, alarmMin, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GoldPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                onClick = {
                                    TimePickerDialog(context, { _, h, m -> viewModel.updateAlarmTime(h, m, true) }, alarmHour, alarmMin, false).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = NavySurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                            ) {
                                val timeStr = String.format("%02d:%02d %s", if (alarmHour % 12 == 0) 12 else alarmHour % 12, alarmMin, if (alarmHour >= 12) "PM" else "AM")
                                Text(text = timeStr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                            }

                            Button(
                                onClick = { viewModel.testAlarmNow() },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Test Loud Alarm", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            AddOrEditCategoryDialog(
                initialCategory = null,
                onDismiss = { showAddCategoryDialog = false },
                onSave = { name, colorHex, emoji ->
                    viewModel.addCategory(name, colorHex, emoji)
                    showAddCategoryDialog = false
                }
            )
        }

        // Edit Category Dialog
        editingCategory?.let { cat ->
            AddOrEditCategoryDialog(
                initialCategory = cat,
                onDismiss = { editingCategory = null },
                onSave = { name, colorHex, emoji ->
                    viewModel.updateCategory(cat.copy(name = name, colorHex = colorHex, iconEmoji = emoji))
                    editingCategory = null
                }
            )
        }

        // Edit Name Dialog
        if (showEditNameDialog) {
            var tempName by remember { mutableStateOf(userProfile?.userName ?: "Emmanuel") }
            Dialog(onDismissRequest = { showEditNameDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth().border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("Edit Name", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavySurfaceBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = NavySurfaceElevated,
                                unfocusedContainerColor = NavySurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = { showEditNameDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceElevated), modifier = Modifier.weight(1f)) {
                                Text("Cancel", color = TextSilver)
                            }
                            Button(onClick = { viewModel.updateUserName(tempName); showEditNameDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest), modifier = Modifier.weight(1f)) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddOrEditCategoryDialog(
    initialCategory: CategoryItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, colorHex: String, emoji: String) -> Unit
) {
    var name by remember { mutableStateOf(initialCategory?.name ?: "") }
    var colorHex by remember { mutableStateOf(initialCategory?.colorHex ?: "#8B5CF6") }
    var emoji by remember { mutableStateOf(initialCategory?.iconEmoji ?: "🏷️") }

    val colors = listOf("#8B5CF6", "#3B82F6", "#10B981", "#F59E0B", "#EC4899", "#06B6D4", "#F43F5E")
    val emojis = listOf("✨", "⚖️", "🌱", "🎬", "🤝", "🌿", "📚", "🎯", "💼", "🛡️")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier.fillMaxWidth().border(1.5.dp, GoldPrimary, RoundedCornerShape(22.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text(
                    text = if (initialCategory == null) "New Category" else "Edit Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = NavySurfaceElevated,
                        unfocusedContainerColor = NavySurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Icon Emoji:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { em ->
                        Surface(
                            onClick = { emoji = em },
                            shape = RoundedCornerShape(8.dp),
                            color = if (emoji == em) GoldPrimary.copy(alpha = 0.3f) else NavySurfaceElevated
                        ) {
                            Text(text = em, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Color:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { hex ->
                        val col = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(if (colorHex == hex) 3.dp else 0.dp, TextWhite, CircleShape)
                                .clickable { colorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceElevated), modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextSilver)
                    }
                    Button(
                        onClick = { if (name.isNotBlank()) onSave(name, colorHex, emoji) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
