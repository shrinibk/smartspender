package com.example.fintechspender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintechspender.ai.AITransactionAnalysisDialog
import com.example.fintechspender.ai.AITransactionAnalyzer
import com.example.fintechspender.components.AppLogoMedium
import com.example.fintechspender.components.AppLogoLarge
import com.example.fintechspender.ui.theme.FintechspenderTheme
import com.example.fintechspender.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FintechspenderTheme {
                FintechApp()
            }
        }
    }
}

// Data classes
data class QuickAction(val icon: ImageVector, val label: String)
data class Transaction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val amount: Float
)

data class Regret(val name: String, val amount: Float)
data class ChatMessage(val text: String, val isFromUser: Boolean)

// Sample data
val quickActions = listOf(
    QuickAction(Icons.Default.Add, "Add Expense")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FintechApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    var weeklyBudget by remember { mutableFloatStateOf(0f) }
    var transactions by remember { mutableStateOf(listOf<Transaction>()) }
    var regrets by remember { mutableStateOf(listOf<Regret>()) }

    val totalExpenditure = remember(transactions) { transactions.sumOf { it.amount.toDouble() }.toFloat() }
    val remainingBudget = weeklyBudget - totalExpenditure

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(containerColor = Color.Black, contentColor = Color.White) {
                BottomNavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    selected = selectedTab == 0
                ) { selectedTab = 0 }
                BottomNavigationItem(icon = Icons.Default.Warning, label = "Regret Log", selected = selectedTab == 1) { selectedTab = 1 }
                BottomNavigationItem(icon = Icons.Default.Chat, label = "Chatbot", selected = selectedTab == 2) { selectedTab = 2 }
                BottomNavigationItem(icon = Icons.Default.Person, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF0F5)) // Baby pink background
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    transactions = transactions,
                    totalExpenditure = totalExpenditure,
                    remainingBudget = remainingBudget,
                    onAddExpense = { newTransaction -> transactions = transactions + newTransaction },
                    onEditExpense = { old, new -> transactions = transactions.map { if (it == old) new else it } },
                    onSetBudget = { newBudget -> weeklyBudget = newBudget },
                    weeklyBudget = weeklyBudget,
                    onAddRegret = { newRegret -> regrets = regrets + newRegret }
                )
                1 -> RegretLogScreen(regrets = regrets)
                2 -> ChatbotScreen()
                3 -> ProfileScreen()
            }
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.weight(1f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else Color(0xFFF5C5C5) // Light pink for unselected
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (selected) Color.White else Color(0xFFF5C5C5) // Light pink for unselected
            )
        }
    }
}

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    totalExpenditure: Float,
    remainingBudget: Float,
    onAddExpense: (Transaction) -> Unit,
    onEditExpense: (Transaction, Transaction) -> Unit,
    onSetBudget: (Float) -> Unit,
    weeklyBudget: Float,
    onAddRegret: (Regret) -> Unit
) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSetBudgetDialog by remember { mutableStateOf(false) }
    var showAIAnalysisDialog by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showNotEnoughFundsDialog by remember { mutableStateOf(false) }
    var pendingExpense by remember { mutableStateOf<Pair<String, Float>?>(null) }
    var expenseToEdit by remember { mutableStateOf<Transaction?>(null) }

    // AI Analyzer instance
    val aiAnalyzer = remember { AITransactionAnalyzer() }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { name, amount ->
                if (amount > remainingBudget) {
                    showAddExpenseDialog = false
                    showNotEnoughFundsDialog = true
                } else {
                    showAddExpenseDialog = false
                    if (weeklyBudget > 0 && amount > (weeklyBudget * 0.3f)) {
                        pendingExpense = name to amount
                        showAIAnalysisDialog = true
                    } else {
                        val newTransaction = Transaction(
                            Icons.Default.ShoppingCart,
                            name,
                            "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())}",
                            amount
                        )
                        onAddExpense(newTransaction)
                    }
                }
            }
        )
    }

    if (expenseToEdit != null) {
        EditExpenseDialog(
            transaction = expenseToEdit!!,
            onDismiss = { expenseToEdit = null },
            onConfirm = { newName, newAmount ->
                val originalTransaction = expenseToEdit!!
                val difference = newAmount - originalTransaction.amount
                if (difference > remainingBudget) {
                    showNotEnoughFundsDialog = true
                } else {
                    val updatedTransaction = originalTransaction.copy(title = newName, amount = newAmount)
                    onEditExpense(originalTransaction, updatedTransaction)
                }
                expenseToEdit = null
            }
        )
    }

    if (showAIAnalysisDialog) {
        pendingExpense?.let { (name, amount) ->
            AITransactionAnalysisDialog(
                merchantName = name,
                amount = amount,
                userBudget = weeklyBudget,
                recentTransactions = transactions.map { it.title },
                aiAnalyzer = aiAnalyzer,
                onDismiss = {
                    showAIAnalysisDialog = false
                    pendingExpense?.let { (name, amount) -> onAddRegret(Regret(name, amount)) }
                    pendingExpense = null
                },
                onApprove = {
                    showAIAnalysisDialog = false
                    pendingExpense?.let { (name, amount) ->
                        val newTransaction = Transaction(
                            Icons.Default.ShoppingCart,
                            name,
                            "Today, ${
                                SimpleDateFormat(
                                    "h:mm a",
                                    Locale.getDefault()
                                ).format(Date())
                            }",
                            amount
                        )
                        onAddExpense(newTransaction)
                    }
                    pendingExpense = null
                },
                onEmergencyClick = {
                    showAIAnalysisDialog = false
                    showEmergencyDialog = true
                }
            )
        }
    }

    if (showEmergencyDialog) {
        EmergencyDialog(
            onDismiss = {
                showEmergencyDialog = false
                pendingExpense?.let { (name, amount) -> onAddRegret(Regret(name, amount)) }
                pendingExpense = null
            },
            onConfirm = { reason ->
                showEmergencyDialog = false
                pendingExpense?.let { (_, amount) ->
                    val emergencyTransaction = Transaction(
                        Icons.Default.Warning,
                        "Emergency: $reason",
                        "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())}",
                        amount
                    )
                    onAddExpense(emergencyTransaction)
                }
                pendingExpense = null
            }
        )
    }

    if (showNotEnoughFundsDialog) {
        NotEnoughFundsDialog(onDismiss = { showNotEnoughFundsDialog = false })
    }

    if (showSetBudgetDialog) {
        SetBudgetDialog(
            onDismiss = { showSetBudgetDialog = false },
            onSetBudget = { amount ->
                onSetBudget(amount)
                showSetBudgetDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { AppHeader() }
        item { WelcomeCard() }
        item { FinancialSummaryCard(totalExpenditure, remainingBudget) { showSetBudgetDialog = true } }
        item { QuickActionsRow { showAddExpenseDialog = true } }
        item { RecentTransactionsCard(transactions, onTransactionClick = { expenseToEdit = it }) }
    }
}

@Composable
fun EditExpenseDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (name: String, amount: Float) -> Unit
) {
    var name by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense", color = Color.Black) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Expense Name") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountFloat = amount.toFloatOrNull()
                    if (name.isNotBlank() && amountFloat != null) {
                        onConfirm(name, amountFloat)
                    }
                }
            ) { Text("Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun NotEnoughFundsDialog(onDismiss: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha_animation"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Not Enough Funds", color = Color.Black) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Red.copy(alpha = alpha),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("You do not have enough funds for this expense.", color = Color.Black)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun EmergencyDialog(onDismiss: () -> Unit, onConfirm: (reason: String) -> Unit) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Emergency Expense", color = Color.Black) },
        text = {
            Column {
                Text("Please provide a reason for this emergency expense.", color = Color.Black)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for expense") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) {
                        onConfirm(reason)
                    }
                }
            ) { Text("Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, amount: Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense", color = Color.Black) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Expense Name") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountFloat = amount.toFloatOrNull()
                    if (name.isNotBlank() && amountFloat != null) {
                        onConfirm(name, amountFloat)
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SetBudgetDialog(onDismiss: () -> Unit, onSetBudget: (amount: Float) -> Unit) {
    var budget by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Weekly Budget", color = Color.Black) },
        text = {
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    budget.toFloatOrNull()?.let {
                        onSetBudget(it)
                        onDismiss()
                    }
                }
            ) { Text("Set") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun FinancialSummaryCard(totalExpenditure: Float, remainingBudget: Float, onSetBudgetClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC0CB)) // Baby pink card background
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                FinancialInfoColumn(
                    "Total Expenditure",
                    "₹${String.format(Locale.getDefault(), "%.2f", totalExpenditure)}"
                )
                FinancialInfoColumn(
                    "Remaining Budget",
                    "₹${String.format(Locale.getDefault(), "%.2f", remainingBudget)}"
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onSetBudgetClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF0F5), // Baby pink background
                    contentColor = Color.Black
                )
            ) {
                Text("Set Weekly Budget")
            }
        }
    }
}

@Composable
fun FinancialInfoColumn(label: String, amount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 16.sp, color = Color.Black)
        Text(amount, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC0CB)) // Baby pink card background
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text("Good Morning,", fontSize = 16.sp, color = Color.Black)
                    Text(
                        "Shrinivas Bhandary",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF91A4)), // Baby pink background for logo
                    Alignment.Center
                ) {
                    AppLogoMedium()
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(onAddExpenseClick: () -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(quickActions) { action ->
            QuickActionItem(action) {
                if (action.label == "Add Expense") onAddExpenseClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionItem(action: QuickAction, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        interactionSource = interactionSource,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)) // Baby pink background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(action.icon, action.label, tint = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    action.label,
                    fontSize = 10.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RecentTransactionsCard(transactions: List<Transaction>, onTransactionClick: (Transaction) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC0CB)) // Baby pink card background
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
            if (transactions.isEmpty()) {
                Text(
                    "No transactions yet.",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                transactions.forEach { transaction ->
                    TransactionItem(transaction, onClick = { onTransactionClick(transaction) })
                    if (transaction != transactions.last()) Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF0F5)), // Baby pink background for transaction icons
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    transaction.icon,
                    transaction.title,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(transaction.subtitle, fontSize = 12.sp, color = Color.Black)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "-₹${String.format(Locale.getDefault(), "%.2f", transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Expense",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun RegretLogScreen(regrets: List<Regret>) {
    val totalSavedMoney = regrets.sumOf { it.amount.toDouble() }.toFloat()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB6C1)) // Baby pink for saved money card
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Total Saved Money",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    "₹${String.format(Locale.getDefault(), "%.2f", totalSavedMoney)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        if (regrets.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        "Regret Log Empty",
                        tint = Color.Black, // Black icon
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No Regrets Yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        "Your aborted expenses appear here.",
                        fontSize = 14.sp,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text(
                        "Regret History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(regrets) { regret -> RegretItem(regret) }
            }
        }
    }
}

@Composable
fun RegretItem(regret: Regret) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC0CB)) // Baby pink card background
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF0F5)), // Baby pink background for regret icons
                    Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        "Regret",
                        tint = Color.Black, // Black icon
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    regret.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
            Text(
                "+₹${String.format(Locale.getDefault(), "%.2f", regret.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Success
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC0CB)) // Baby pink card background
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    Alignment.Center
                ) {
                    AppLogoLarge()
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Shrinivas Bhandary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "shrinivas.bhandary@email.com",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ChatbotScreen() {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "Hello! I'm your AI financial assistant. How can I help you today?",
                    false
                )
            )
        )
    }
    var userInput by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Header with logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLogoMedium()
            Spacer(Modifier.width(12.dp))
            Text(
                "Financial Assistant",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Ask something...") },
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (userInput.isNotBlank()) {
                    val userMessage = ChatMessage(userInput, true)
                    val botResponse = getBotResponse(userInput)
                    messages = messages + userMessage + botResponse
                    userInput = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor =
        if (message.isFromUser) MaterialTheme.colorScheme.primary else Color(0xFFFFC0CB) // Baby pink for bot messages
    val textColor = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary else Color.Black

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = textColor
            )
        }
    }
}

fun getBotResponse(userInput: String): ChatMessage {
    val response = when {
        "budget" in userInput.lowercase() -> {
            "A budget is your financial roadmap! 📊 It helps you plan where every rupee goes. Try the 50-30-20 rule: 50% for needs, 30% for wants, and 20% for savings. Would you like help setting up your budget?"
        }

        "saving" in userInput.lowercase() || "save" in userInput.lowercase() -> {
            "Great question about saving! 💰 Start small - even ₹100/month adds up. Use automated transfers to make it easier. Emergency fund first (3-6 months expenses), then goals like vacation or gadgets!"
        }

        "debt" in userInput.lowercase() || "loan" in userInput.lowercase() -> {
            "Debt management is crucial! 💳 Use the snowball method: pay minimums on all debts, then put extra money toward the smallest debt. Or try avalanche method - tackle highest interest rates first."
        }

        "invest" in userInput.lowercase() -> {
            "Investing helps your money grow! 📈 For beginners, consider SIP in mutual funds. Start with equity funds for long-term goals (5+ years) and debt funds for short-term. Always diversify!"
        }

        "emergency" in userInput.lowercase() -> {
            "Emergency funds are your financial safety net! 🛡️ Aim for 3-6 months of expenses in a savings account. This covers unexpected costs like medical bills or job loss without deriving debt."
        }

        "credit card" in userInput.lowercase() -> {
            "Credit cards can be helpful if used wisely! 💳 Always pay full amount by due date to avoid interest. Use for rewards and building credit score, but never spend more than you can afford."
        }

        "mutual fund" in userInput.lowercase() || "sip" in userInput.lowercase() -> {
            "SIPs in mutual funds are perfect for beginners! 📊 They average out market volatility and build discipline. Start with diversified equity funds for wealth creation over 5+ years."
        }

        "tax" in userInput.lowercase() -> {
            "Tax planning saves money! 💡 Use Section 80C (ELSS, PPF, insurance) for ₹1.5L deduction. Section 80D for health insurance. Start early in the financial year for better planning."
        }

        "insurance" in userInput.lowercase() -> {
            "Insurance protects your finances! 🛡️ Term life insurance (10-15x annual income) and health insurance are must-haves. Avoid mixing insurance with investment unless necessary."
        }

        "expense" in userInput.lowercase() || "spending" in userInput.lowercase() -> {
            "Track your expenses to understand spending patterns! 📱 Use the 24-hour rule for non-essential purchases above ₹1000. Categorize: needs vs wants. This app helps with that!"
        }

        "goal" in userInput.lowercase() -> {
            "Financial goals give direction! 🎯 Set SMART goals: Specific, Measurable, Achievable, Relevant, Time-bound. Short-term (1 year), medium-term (3-5 years), long-term (5+ years)."
        }

        "retirement" in userInput.lowercase() -> {
            "Start retirement planning early! ⏰ Power of compounding works best with time. Contribute to EPF, PPF, and SIP in equity mutual funds. Target 25-30x annual expenses by retirement."
        }

        "impulsive" in userInput.lowercase() || "impulse" in userInput.lowercase() -> {
            "Impulse buying is common! 🛒 This app's AI helps identify such purchases. Use the 24-hour rule, unsubscribe from shopping apps, and ask 'Do I really need this?' before buying."
        }

        userInput.lowercase().contains("hello") || userInput.lowercase().contains("hi") -> {
            "Hello! 👋 I'm your AI financial assistant. I can help with budgeting, saving, investing, debt management, and more. What financial topic interests you today?"
        }

        userInput.lowercase().contains("help") -> {
            "I'm here to help! 🤝 Ask me about:\n• Budgeting & expense tracking\n• Saving strategies\n• Investment basics\n• Debt management\n• Emergency funds\n• Tax planning\n\nWhat would you like to know?"
        }

        else -> {
            "That's an interesting question! 🤔 I specialize in personal finance topics like budgeting, saving, investing, debt management, and expense tracking. Could you rephrase your question or ask about one of these areas?"
        }
    }
    return ChatMessage(response, false)
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLogoMedium()
        Spacer(Modifier.width(12.dp))
        Text(
            "Fintech Spender",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FintechAppPreview() {
    FintechspenderTheme {
        FintechApp()
    }
}
