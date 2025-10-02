package com.avsdeveloper.pomodoro.presentation.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import org.koin.compose.koinInject

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🍅 Pomodoro Timer",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            SessionTypeChip(state.timer.sessionType)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.formattedTime,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = when (state.timer.sessionType) {
                    SessionType.WORK -> MaterialTheme.colorScheme.primary
                    SessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                    SessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Session ${state.timer.sessionCount}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(48.dp))

            TimerControls(
                timerState = state.timer.timerState,
                onStart = { viewModel.handleIntent(TimerIntent.StartTimer) },
                onPause = { viewModel.handleIntent(TimerIntent.PauseTimer) },
                onReset = { viewModel.handleIntent(TimerIntent.ResetTimer) },
                onNext = { viewModel.handleIntent(TimerIntent.StartNextSession) }
            )
        }
    }
}

@Composable
fun SessionTypeChip(sessionType: SessionType) {
    val (text, emoji) = when (sessionType) {
        SessionType.WORK -> "Work Session" to "💼"
        SessionType.SHORT_BREAK -> "Short Break" to "☕"
        SessionType.LONG_BREAK -> "Long Break" to "🌴"
    }

    SuggestionChip(
        onClick = { },
        label = {
            Text("$emoji $text")
        }
    )
}

@Composable
fun TimerControls(
    timerState: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (timerState) {
            TimerState.IDLE, TimerState.PAUSED -> {
                Button(
                    onClick = onStart,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Start")
                }
            }
            TimerState.RUNNING -> {
                Button(
                    onClick = onPause,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Pause")
                }
            }
            TimerState.COMPLETED -> {
                Button(
                    onClick = onNext,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Next")
                }
            }
        }

        if (timerState != TimerState.COMPLETED) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.width(120.dp)
            ) {
                Text("Reset")
            }
        }
    }
}