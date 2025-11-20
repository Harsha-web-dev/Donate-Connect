@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.donationconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============ MAIN ACTIVITY ============
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonationAppTheme {
                AppNavigation()
            }
        }
    }
}

// ============ USER DATABASE (In-Memory) ============
object UserDatabase {
    private val users = mutableListOf<User>()

    init {
        // Pre-registered demo users
        users.add(User("demo@gmail.com", "123456", "Demo User", "donator", "9876543210"))
        users.add(User("receiver@gmail.com", "123456", "John Doe", "receiver", "9876543211"))
    }

    fun register(email: String, password: String, name: String, userType: String, phone: String): Boolean {
        if (users.any { it.email == email }) return false
        users.add(User(email, password, name, userType, phone))
        return true
    }

    fun login(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }
}

data class User(
    val email: String,
    val password: String,
    val name: String,
    val userType: String,
    val phone: String
)

// ============ ITEMS DATABASE ============
object ItemsDatabase {
    val allItems = mutableStateListOf(
        DonationItem("1", "Wheelchair", "Manual wheelchair in excellent condition. Lightly used for 6 months only. All wheels and brakes working perfectly.", "Medical", "2 days ago", "Bengaluru, Karnataka", "Available", "Ravi Kumar", "9876543210"),
        DonationItem("2", "Educational Books Set", "Complete set of 10th standard textbooks. All subjects included. Very good condition.", "Education", "1 week ago", "Mumbai, Maharashtra", "Available", "Priya Sharma", "9876543211"),
        DonationItem("3", "Cotton Shorts (5 pieces)", "Comfortable cotton shorts for men. Size L. Barely used, washed and clean.", "Clothing", "3 days ago", "Delhi, NCR", "Available", "Amit Patel", "9876543212"),
        DonationItem("4", "Electric Blender", "Philips blender 750W. Works perfectly. 3 years old but well maintained.", "Appliance", "5 days ago", "Chennai, Tamil Nadu", "Available", "Lakshmi Iyer", "9876543213"),
        DonationItem("5", "Walking Crutches", "Adjustable aluminum crutches. Height adjustable 4'5\" to 5'10\". Like new condition.", "Medical", "1 day ago", "Pune, Maharashtra", "Available", "Vijay Singh", "9876543214"),
        DonationItem("6", "Laptop Bag", "Branded laptop bag with multiple compartments. Fits 15.6 inch laptop. Good condition.", "Accessories", "4 days ago", "Hyderabad, Telangana", "Available", "Sneha Reddy", "9876543215"),
        DonationItem("7", "Baby Stroller", "Foldable baby stroller in good working condition. Comfortable seat with safety belt.", "Baby Items", "6 days ago", "Kolkata, West Bengal", "Available", "Anita Das", "9876543216"),
        DonationItem("8", "Blood Pressure Monitor", "Digital BP monitor with large display. Accurate readings. Used only 10 times.", "Medical", "2 days ago", "Bengaluru, Karnataka", "Available", "Dr. Suresh", "9876543217"),
        DonationItem("9", "Cricket Kit", "Complete cricket kit with bat, pads, gloves, helmet. Good for practice.", "Sports", "1 week ago", "Ahmedabad, Gujarat", "Available", "Rohan Mehta", "9876543218"),
        DonationItem("10", "Study Table & Chair", "Wooden study table with chair. Compact size perfect for small rooms.", "Furniture", "3 days ago", "Jaipur, Rajasthan", "Available", "Kavita Sharma", "9876543219"),
        DonationItem("11", "Electric Kettle", "1.5L electric kettle. Boils water in 3 minutes. Auto shut-off feature.", "Appliance", "5 days ago", "Lucknow, Uttar Pradesh", "Available", "Mohan Kumar", "9876543220"),
        DonationItem("12", "Winter Jackets (3 pieces)", "Warm winter jackets for adults. Size M, L, XL. Clean and ready to use.", "Clothing", "4 days ago", "Delhi, NCR", "Available", "Rahul Verma", "9876543221"),
        DonationItem("13", "Pressure Cooker", "5 litre aluminium pressure cooker. Hawkins brand. Works perfectly.", "Appliance", "2 days ago", "Indore, Madhya Pradesh", "Available", "Sunita Joshi", "9876543222"),
        DonationItem("14", "School Bags (2 pieces)", "Sturdy school bags with multiple pockets. Suitable for primary school kids.", "Education", "1 week ago", "Surat, Gujarat", "Available", "Kiran Patel", "9876543223"),
        DonationItem("15", "Bedsheets Set", "5 cotton bedsheets with pillow covers. Colorful prints. Washed and ironed.", "Home", "3 days ago", "Nagpur, Maharashtra", "Available", "Meera Singh", "9876543224")
    )

    fun addItem(item: DonationItem) {
        allItems.add(0, item)
    }

    fun getUserItems(userName: String, userType: String): List<DonationItem> {
        return if (userType == "donator") {
            allItems.filter { it.donorName == userName }
        } else {
            allItems.filter { it.status == "Requested" }
        }
    }
}

// ============ THEME ============
@Composable
fun DonationAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2E7D5F),
            secondary = Color(0xFF4A9B7F),
            tertiary = Color(0xFF78B89F)
        ),
        content = content
    )
}

// ============ APP NAVIGATION ============
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("splash") }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var selectedItem by remember { mutableStateOf<DonationItem?>(null) }

    when {
        currentScreen == "splash" -> {
            SplashScreen(onGetStarted = { currentScreen = "login" })
        }
        currentScreen == "login" -> {
            LoginScreen(
                onLoginSuccess = { user ->
                    currentUser = user
                    currentScreen = "main"
                },
                onSignUpClick = { currentScreen = "signup" }
            )
        }
        currentScreen == "signup" -> {
            SignUpScreen(
                onSignUpSuccess = { user ->
                    currentUser = user
                    currentScreen = "main"
                },
                onBackToLogin = { currentScreen = "login" }
            )
        }
        currentScreen == "main" && currentUser != null -> {
            MainApp(
                user = currentUser!!,
                onLogout = {
                    currentUser = null
                    currentScreen = "login"
                },
                onItemClick = { item ->
                    selectedItem = item
                    currentScreen = "detail"
                }
            )
        }
        currentScreen == "detail" && selectedItem != null -> {
            ItemDetailScreen(
                item = selectedItem!!,
                onBack = { currentScreen = "main" },
                currentUser = currentUser!!
            )
        }
    }
}

// ============ SPLASH SCREEN ============
@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "DONATE\nREUSE",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Donate. Reuse, Make a Difference.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            Icons.Default.FavoriteBorder,
            contentDescription = "Illustration",
            modifier = Modifier.size(200.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Get Started", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Demo Credentials:\ndemo@gmail.com / 123456",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============ LOGIN SCREEN ============
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            Icons.Default.Favorite,
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "DONATE REUSE",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("LOGIN") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    onSignUpClick()
                },
                text = { Text("SIGN UP") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) }
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please fill all fields"
                } else {
                    val user = UserDatabase.login(email, password)
                    if (user != null) {
                        onLoginSuccess(user)
                    } else {
                        errorMessage = "Invalid email or password"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("LOGIN", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Demo Credentials:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Email: demo@gmail.com", fontSize = 12.sp)
                Text("Password: 123456", fontSize = 12.sp)
            }
        }
    }
}

// ============ SIGN UP SCREEN ============
@Composable
fun SignUpScreen(
    onSignUpSuccess: (User) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("donator") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            Icons.Default.Favorite,
            contentDescription = "Logo",
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "Create Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = ""
            },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                errorMessage = ""
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Phone, null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Password (min 6 characters)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (userType == "donator") "I want to Donate" else "I need Items",
                onValueChange = {},
                readOnly = true,
                label = { Text("Account Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                leadingIcon = { Icon(Icons.Default.AccountCircle, null) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("I want to Donate") },
                    onClick = {
                        userType = "donator"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("I need Items") },
                    onClick = {
                        userType = "receiver"
                        expanded = false
                    }
                )
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() -> {
                        errorMessage = "Please fill all fields"
                    }
                    password.length < 6 -> {
                        errorMessage = "Password must be at least 6 characters"
                    }
                    !email.contains("@") -> {
                        errorMessage = "Please enter a valid email"
                    }
                    phone.length != 10 -> {
                        errorMessage = "Please enter a valid 10-digit phone number"
                    }
                    else -> {
                        val success = UserDatabase.register(email, password, name, userType, phone)
                        if (success) {
                            val user = User(email, password, name, userType, phone)
                            onSignUpSuccess(user)
                        } else {
                            errorMessage = "Email already registered"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SIGN UP", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login")
        }
    }
}

// ============ MAIN APP ============
@Composable
fun MainApp(
    user: User,
    onLogout: () -> Unit,
    onItemClick: (DonationItem) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ShareCare", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, "Browse") },
                    label = { Text("Browse") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, "Search") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Add, "Post") },
                    label = { Text(if (user.userType == "donator") "Donate" else "Request") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { selectedTab = 2 },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> BrowseScreen(Modifier.padding(padding), user, onItemClick)
            1 -> SearchScreen(Modifier.padding(padding), onItemClick)
            2 -> if (user.userType == "donator") {
                PostItemScreen(Modifier.padding(padding), user) { selectedTab = 0 }
            } else {
                RequestItemScreen(Modifier.padding(padding), user) { selectedTab = 0 }
            }
            3 -> ProfileScreen(Modifier.padding(padding), user, onLogout)
        }
    }
}

// ============ BROWSE SCREEN ============
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    user: User,
    onItemClick: (DonationItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredItems = if (selectedCategory == "All") {
        ItemsDatabase.allItems
    } else {
        ItemsDatabase.allItems.filter { it.category == selectedCategory }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Welcome, ${user.name}!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Explore ${ItemsDatabase.allItems.size} items available for donation",
                    fontSize = 14.sp
                )
            }
        }

        // Category Filter
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Medical", "Education", "Clothing", "Appliance").forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Available Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(filteredItems) { item ->
                ItemCard(item, onItemClick)
            }
        }
    }
}

// ============ ITEM CARD ============
@Composable
fun ItemCard(item: DonationItem, onItemClick: (DonationItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onItemClick(item) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.description,
                        fontSize = 13.sp,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp))
                        Text(item.location, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp))
                        Text(item.postedTime, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        item.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }

                Text(
                    item.status,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ============ ITEM DETAIL SCREEN ============
@Composable
fun ItemDetailScreen(
    item: DonationItem,
    onBack: () -> Unit,
    currentUser: User
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                item.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    item.category,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(item.description, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Donor", item.donorName)
                    DetailRow("Contact", item.phone)
                    DetailRow("Location", item.location)
                    DetailRow("Posted", item.postedTime)
                    DetailRow("Status", item.status)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentUser.userType == "receiver" && item.status == "Available") {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Phone, "Request")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request This Item")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Request Sent!") },
                    text = {
                        Text("Your request has been sent to the donor. They will contact you at ${currentUser.phone}")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            onBack()
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ============ SEARCH SCREEN ============
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onItemClick: (DonationItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredItems = ItemsDatabase.allItems.filter { item ->
        val matchesSearch = searchQuery.isEmpty() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Search Items", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by item, location...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Filter by Category", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Medical", "Education", "Clothing", "Appliance", "Sports", "Furniture")) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("${filteredItems.size} items found", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems) { item ->
                ItemCard(item, onItemClick)
            }
        }
    }
}

// ============ POST ITEM SCREEN ============
@Composable
fun PostItemScreen(
    modifier: Modifier = Modifier,
    user: User,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Medical") }
    var location by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Donate an Item", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Help someone in need", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Item Title *") },
            placeholder = { Text("e.g., Wheelchair, Books, etc.") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description *") },
            placeholder = { Text("Describe the condition and details") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Medical", "Education", "Clothing", "Appliance", "Sports", "Furniture", "Baby Items", "Accessories", "Home", "Other").forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location *") },
            placeholder = { Text("e.g., Bengaluru, Karnataka") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.LocationOn, "Location") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty() && location.isNotEmpty()) {
                    val newItem = DonationItem(
                        id = (ItemsDatabase.allItems.size + 1).toString(),
                        title = title,
                        description = description,
                        category = category,
                        postedTime = "Just now",
                        location = location,
                        status = "Available",
                        donorName = user.name,
                        phone = user.phone
                    )
                    ItemsDatabase.addItem(newItem)
                    showSuccessDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty() && description.isNotEmpty() && location.isNotEmpty()
        ) {
            Icon(Icons.Default.Send, "Post")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Post Donation")
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    title = ""
                    description = ""
                    location = ""
                    onSuccess()
                },
                title = { Text("Success!") },
                text = { Text("Your donation has been posted. People in need can now see it!") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        title = ""
                        description = ""
                        location = ""
                        onSuccess()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// ============ REQUEST ITEM SCREEN ============
@Composable
fun RequestItemScreen(
    modifier: Modifier = Modifier,
    user: User,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Medical") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Request an Item", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Let donors know what you need", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Item Needed *") },
            placeholder = { Text("e.g., Wheelchair") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Why you need this *") },
            placeholder = { Text("Explain your situation") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Medical", "Education", "Clothing", "Appliance", "Other").forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    showSuccessDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty() && description.isNotEmpty()
        ) {
            Icon(Icons.Default.Send, "Submit")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submit Request")
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    title = ""
                    description = ""
                    onSuccess()
                },
                title = { Text("Request Submitted!") },
                text = { Text("Your request has been posted. Donors will contact you soon!") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        title = ""
                        description = ""
                        onSuccess()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// ============ PROFILE SCREEN ============
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val userItems = ItemsDatabase.getUserItems(user.name, user.userType)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(user.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user.email, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (user.userType == "donator") "DONATOR" else "RECEIVER",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Items", userItems.size.toString())
            StatCard("Helped", "12")
            StatCard("Rating", "4.8★")
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (user.userType == "donator") "My Donations" else "My Requests") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("History") }
            )
        }

        // Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (userItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                "No items",
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (user.userType == "donator") "No donations yet" else "No requests yet",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (user.userType == "donator") "Start donating to help others" else "Request items you need",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(userItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(0.3f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                Text(item.location, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    item.status,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.ExitToApp, "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp)
        }
    }
}

// ============ DATA MODEL ============
data class DonationItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val postedTime: String,
    val location: String,
    val status: String,
    val donorName: String,
    val phone: String
)