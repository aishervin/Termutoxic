package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

data class SandboxOutput(val command: String, val output: String, val isError: Boolean)

@Composable
fun SandboxScreen(viewModel: MainViewModel) {
    val scope = rememberCoroutineScope()
    val isFa = viewModel.currentLanguage.collectAsState().value == "fa"

    var commandInput by remember { mutableStateOf("") }
    val outputs = remember { mutableStateListOf<SandboxOutput>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Terminal, contentDescription = "Sandbox", tint = Color(0xFF00E5FF))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFa) "💻 سندباکس و ترمینال تعاملی" else "💻 Interactive Sandbox & Terminal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF0F6FC)
                )
            }

            IconButton(onClick = { outputs.clear() }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF8B949E))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Terminal Console Output Box
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF010409)),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (outputs.isEmpty()) {
                    item {
                        Text(
                            text = if (isFa)
                                "دستور خود را در ترمینال سندباکس وارد کنید (مثال: uname -a, pkg list-installed, node -v)..."
                            else
                                "Enter shell command in sandbox terminal (e.g. uname -a, pkg list-installed, node -v)...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }

                items(outputs) { item ->
                    Column {
                        Text(
                            text = "$ ~ ${item.command}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.output,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (item.isError) Color(0xFFFF5252) else Color(0xFF00E676)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input and Run
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("sandbox_command_input"),
                placeholder = { Text(if (isFa) "دستور ترمینال..." else "Shell command...") },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFFF0F6FC)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        val cmd = commandInput
                        commandInput = ""
                        scope.launch {
                            val res = viewModel.termuxManager.runShellCommand(cmd)
                            val combined = if (res.stdout.isNotEmpty()) res.stdout else res.stderr
                            outputs.add(
                                SandboxOutput(
                                    command = cmd,
                                    output = if (combined.isNotEmpty()) combined else "(Executed with exit code ${res.exitCode})",
                                    isError = res.exitCode != 0
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.testTag("sandbox_run_button")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run")
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isFa) "اجرا" else "Run")
            }
        }
    }
}
