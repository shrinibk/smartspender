package com.example.fintechspender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintechspender.ui.theme.*
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
    val amount: Float // Changed to Float
)
data class Account(val name: String, val type: String, val balance: String)

// Sample data
val quickActions = listOf(
    QuickAction(Icons.Default.Add, "Add Expense")
)

val accounts = listOf(
    Account("Main Checking", "Checking Account", "₹6,95,459.32"),
    Account("Savings", "Savings Account", "₹3,00,000"),
    Account("Investment", "Investment Account", "₹12,50,234.56"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FintechApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Hoisted state for financial data
    var weeklyBudget by remember { mutableFloatStateOf(0f) }
    var transactions by remember { mutableStateOf(listOf<Transaction>()) }
    val totalExpenditure = remember(transactions) {
        transactions.sumOf { it.amount.toDouble() }.toFloat()
    }
    val remainingBudget = weeklyBudget - totalExpenditure

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(containerColor = DeepPurple, contentColor = White) {
                BottomNavigationItem(icon = Icons.Default.Home, label = "Home", selected = selectedTab == 0) { selectedTab = 0 }
                BottomNavigationItem(icon = Icons.Default.AccountBox, label = "Accounts", selected = selectedTab == 1) { selectedTab = 1 }
                BottomNavigationItem(icon = Icons.Default.Warning, label = "Regret Log", selected = selectedTab == 2) { selectedTab = 2 }
                BottomNavigationItem(icon = Icons.Default.Person, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleBackground)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    transactions = transactions,
                    totalExpenditure = totalExpenditure,
                    remainingBudget = remainingBudget,
                    onAddExpense = { newTransaction ->
                        transactions = transactions + newTransaction
                    },
                    onSetBudget = { newBudget ->
                        weeklyBudget = newBudget
                    }
                )
                1 -> AccountsScreen()
                2 -> RegretLogScreen()
                3 -> ProfileScreen()
            }
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.weight(1f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = if (selected) White else MediumGray)
            Text(text = label, fontSize = 10.sp, color = if (selected) White else MediumGray)
        }
    }
}

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    totalExpenditure: Float,
    remainingBudget: Float,
    onAddExpense: (Transaction) -> Unit,
    onSetBudget: (Float) -> Unit
) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSetBudgetDialog by remember { mutableStateOf(false) }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onAddExpense = { name, amount ->
                val newTransaction = Transaction(
                    icon = Icons.Default.ShoppingCart,
                    title = name,
                    subtitle = "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())}",
                    amount = amount
                )
                onAddExpense(newTransaction)
                showAddExpenseDialog = false
            }
        )
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { WelcomeCard() }
        item {
            FinancialSummaryCard(
                totalExpenditure = totalExpenditure,
                remainingBudget = remainingBudget,
                onSetBudgetClick = { showSetBudgetDialog = true }
            )
        }
        item { QuickActionsRow(onAddExpenseClick = { showAddExpenseDialog = true }) }
        item { RecentTransactionsCard(transactions = transactions) }
    }
}

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAddExpense: (name: String, amount: Float) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Expense Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountFloat = amount.toFloatOrNull()
                    if (name.isNotBlank() && amountFloat != null) {
                        onAddExpense(name, amountFloat)
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SetBudgetDialog(onDismiss: () -> Unit, onSetBudget: (amount: Float) -> Unit) {
    var budget by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Weekly Budget") },
        text = {
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val budgetFloat = budget.toFloatOrNull()
                    if (budgetFloat != null) {
                        onSetBudget(budgetFloat)
                    }
                }
            ) { Text("Set") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FinancialSummaryCard(
    totalExpenditure: Float,
    remainingBudget: Float,
    onSetBudgetClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DeepPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FinancialInfoColumn("Total Expenditure", "₹${String.format(Locale.getDefault(), "%.2f", totalExpenditure)}")
                FinancialInfoColumn("Remaining Budget", "₹${String.format(Locale.getDefault(), "%.2f", remainingBudget)}")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onSetBudgetClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = VeryLightPurple, contentColor = DeepPurple)
            ) {
                Text("Set Weekly Budget")
            }
        }
    }
}

@Composable
fun FinancialInfoColumn(label: String, amount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 16.sp, color = VeryLightPurple)
        Text(text = amount, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good Morning,", fontSize = 16.sp, color = DarkGray)
                    Text("Alex Johnson", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(DeepPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(onAddExpenseClick: () -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(quickActions) { action ->
            QuickActionItem(action, onClick = {
                if (action.label == "Add Expense") {
                    onAddExpenseClick()
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionItem(action: QuickAction, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, contentDescription = action.label, tint = DeepPurple, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(action.label, fontSize = 10.sp, color = DeepPurple, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RecentTransactionsCard(transactions: List<Transaction>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
            Spacer(Modifier.height(16.dp))

            if (transactions.isEmpty()) {
                Text("No transactions yet.", color = MediumGray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                transactions.forEach { transaction ->
                    TransactionItem(transaction)
                    if (transaction != transactions.last()) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(DeepPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(transaction.icon, contentDescription = transaction.title, tint = White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(transaction.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DarkGray)
                Text(transaction.subtitle, fontSize = 12.sp, color = MediumGray)
            }
        }
        val amountText = "-₹${String.format(Locale.getDefault(), "%.2f", transaction.amount)}"
        Text(
            text = amountText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
    }
}

@Composable
fun AccountsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("My Accounts", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
        }
        items(accounts) { account -> AccountCard(account) }
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(account.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
                    Text(account.type, fontSize = 14.sp, color = MediumGray)
                }
                Text(account.balance, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
            }
        }
    }
}

@Composable
fun RegretLogScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = "Regret Log", tint = DeepPurple, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Track Your Regretful Spends", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
        Text("Reflect on your spending habits.", fontSize = 14.sp, color = MediumGray, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(DeepPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = White, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Alex Johnson", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepPurple)
                Text("alex.johnson@email.com", fontSize = 14.sp, color = MediumGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FintechAppPreview() {
    FintechspenderTheme {
        FintechApp()
    }
}
