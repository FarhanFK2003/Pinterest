package com.example.pinterest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pinterest.ui.theme.PinterestTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PinterestTheme {
                AppNavigation()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PinterestUI() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pinterest logo at the top
            Image(
                painter = painterResource(id = R.drawable.pinterest_logo), // Use the Pinterest icon
                contentDescription = "Pinterest Logo",
                modifier = Modifier
                    .size(150.dp)  // Adjusted logo size
                    .padding(bottom = 32.dp),  // More space below the logo
                //contentScale = ContentScale.Crop
            )

            // Welcome text
            Text(
                text = "Welcome to Pinterest",
                fontSize = 22.sp,  // Adjusted font size
                modifier = Modifier.padding(bottom = 24.dp)  // Increased space below the text
            )

            // Email input field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle input change */ },
                label = { Text(text = "Email address") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)  // Adjusted width
                    .padding(bottom = 24.dp),  // Increased space below the input field
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red,
                    errorCursorColor = Color.Red
                )
            )

            // Continue button
            Button(
                onClick = { /* No logic needed */ },
                modifier = Modifier
                    .width(300.dp)  // Adjusted width
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Continue", color = Color.White)
            }

            // Continue with Facebook button
            Button(
                onClick = { /* No logic needed */ },
                modifier = Modifier
                    .width(300.dp)  // Adjusted width
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Text(text = "Continue with Facebook", color = Color.White)
            }

            // Continue with Google button
            Button(
                onClick = { /* No logic needed */ },
                modifier = Modifier
                    .width(300.dp),  // Adjusted width
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(text = "Continue with Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(48.dp))  // Increased space before privacy text

            // Privacy policy and terms
            Text(
                text = "By continuing, you agree to Pinterest's Terms of Service and acknowledge that you've read our Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 32.dp)  // Adjusted padding for alignment
                    .align(Alignment.CenterHorizontally),  // Ensure centered alignment
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinterestUIPreview() {
    PinterestTheme {
        PinterestUI()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Log in",
                color = Color.Black,
                modifier = Modifier.padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally),  // Ensure centered alignment
                fontSize = 22.sp,  // Adjusted font size
            )
            // Facebook Login Button
            Button(
                onClick = { /* Handle Facebook login */ },
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.facebook_icon),  // Facebook icon resource
//                    contentDescription = "Facebook Logo",
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Facebook", color = Color.White)
            }

            // Google Login Button
            Button(
                onClick = { /* Handle Google login */ },
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)), // White background
                border = BorderStroke(1.dp, Color.Gray)
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.google_icon),  // Google icon resource
//                    contentDescription = "Google Logo",
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google", color = Color.Black)
            }

            // OR Divider Text
            Text(
                text = "Or",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Email Input Field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle email input */ },
                label = { Text(text = "Email Address") },  // Pre-filled with email
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Gray
                ),
//                leadingIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.email_icon), // Optional email icon
//                        contentDescription = "Email Icon"
//                    )
//                }
            )

            // Password Input Field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle password input */ },
                label = { Text(text = "Enter your password") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Gray
                ),
//                leadingIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.password_icon), // Optional password icon
//                        contentDescription = "Password Icon"
//                    )
//                },
//                trailingIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.visibility_icon),  // Optional eye/visibility icon
//                        contentDescription = "Toggle Password Visibility"
//                    )
//                }
            )

            // Log In Button
            Button(
                onClick = { /* Handle login */ },
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))  // Red button background
            ) {
                Text(text = "Log In", color = Color.White)
            }

            // Forgot Password
            Text(
                text = "Forgot password?",
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PinterestTheme {
        LoginScreen()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "All",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 22.sp
            )
            ImagesGrid() // The grid of images
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Home")
            },
            label = {
                Text(text = "Home")
            },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            label = {
                Text(text = "Search")
            },
            selected = currentRoute == "search",
            onClick = {
                navController.navigate("search") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Add, contentDescription = "Create")
            },
            label = {
                Text(text = "Create")
            },
            selected = false,
            onClick = { /* Handle create click */ }
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            },
            label = {
                Text(text = "Notifications")
            },
            selected = currentRoute == "notifications",
            onClick = {
                navController.navigate("notifications") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Person, contentDescription = "Saved")
            },
            label = {
                Text(text = "Saved")
            },
            selected = currentRoute == "saved",
            onClick = {
                navController.navigate("saved") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun ImagesGrid() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First row of images
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageCard(R.drawable.image1)
            ImageCard(R.drawable.image2)
        }
        Spacer(modifier = Modifier.height(16.dp)) // Space between rows
        // Second row of images
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageCard(R.drawable.image3)
            ImageCard(R.drawable.image4)
        }
        Spacer(modifier = Modifier.height(16.dp)) // Space between rows
        // Third row of images
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageCard(R.drawable.image1)
            ImageCard(R.drawable.image2)
        }
        Spacer(modifier = Modifier.height(16.dp)) // Space between rows
        // Fourth row of images
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageCard(R.drawable.image3)
            ImageCard(R.drawable.image4)
        }
    }
}

@Composable
fun ImageCard(imageRes: Int) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchUI(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle input change */ },
                label = { Text(text = "Search for Ideas") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost defines the navigation graph
    NavHost(navController = navController, startDestination = "home") {
        // Home screen route
        composable("home") {
            HomeScreen(navController)  // Pass navController to navigate to other screens
        }
        // Search screen route
        composable("search") {
            SearchUI(navController)  // Search screen
        }
        // Notifications screen route
        composable("notifications") {
            NotificationsScreen(navController)
        }
        // Saved screen route
        composable("saved") {
            SavedScreenPins(navController) // Navigating to SavedScreenPins
        }

        composable("updates") {
            NotificationsScreen(navController) // Updates screen
        }

        composable("inbox") {
            NotificationsInboxScreen(navController) // Updates screen
        }
        
        composable("pins") {
            SavedScreenPins(navController)
        }

        composable("boards") {
            SavedScreenBoards(navController)
        }

        composable("account") {
            AccountScreen(navController)
        }

        composable("avatar") {
            AvatarScreen(navController)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppNavigation()
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val mockNavController = rememberNavController() // A mock NavController for preview purposes
    SearchUI(navController = mockNavController) // Directly preview the SearchUI
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotificationsScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        topBar = {
            NotificationsTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)
        }
    ) {
        // Added padding for spacing between the top bar and notifications list
        NotificationsList(
            notifications = sampleNotifications(),
            modifier = Modifier.padding(top = 36.dp) // Padding to create space between top bar and list
        )
    }
}

@Composable
fun NotificationsTopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Tabs for "Updates" and "Inbox"
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Updates",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        // Navigate to Updates Screen when clicked
                        navController.navigate("updates")
                    }

            )
            Text(
                text = "Inbox",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        // Navigate to Inbox Screen when clicked
                        navController.navigate("inbox")
                    }

            )
        }
    }
}

@Composable
fun NotificationsList(notifications: List<NotificationItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(notifications) { notification ->
            NotificationItemRow(notification)
        }
    }
}

@Composable
fun NotificationItemRow(notification: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Image(
            painter = painterResource(id = notification.imageRes),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        // Notification Text
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.subtitle,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Time
        Text(
            text = notification.time,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// Sample data model for notification
data class NotificationItem(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val time: String
)

fun sampleNotifications(): List<NotificationItem> {
    return listOf(
        NotificationItem(R.drawable.image1, "Comics Art for you", "", "4h"),
        NotificationItem(R.drawable.image2, "You have a good eye", "", "14h"),
        NotificationItem(R.drawable.image3, "Inspired by you", "", "18h"),
        NotificationItem(R.drawable.image4, "Disney Wallpaper for you", "", "22h"),
        NotificationItem(R.drawable.image1, "You have a good eye", "", "1d"),
        NotificationItem(R.drawable.image2, "Wallpaper for you", "", "1d")
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    NotificationsScreen(navController = mockNavController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotificationsInboxScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        topBar = {
            NotificationsInboxTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)
        }
    ) {
        // Added padding for spacing between the top bar and inbox list
        NotificationsInboxList(
            notifications = sampleInboxNotifications(),
            modifier = Modifier.padding(top = 36.dp) // Padding to create space between top bar and list
        )
    }
}

@Composable
fun NotificationsInboxTopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Tabs for "Updates" and "Inbox"
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Updates",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("updates")
                    }
            )
            Text(
                text = "Inbox",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("inbox")
                    }
            )
        }
    }
}

@Composable
fun NotificationsInboxList(notifications: List<NotificationItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(notifications) { notification ->
            NotificationInboxItemRow(notification)
        }
    }
}

@Composable
fun NotificationInboxItemRow(notification: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Image(
            painter = painterResource(id = notification.imageRes),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        // Notification Text
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.subtitle,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Time
        Text(
            text = notification.time,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// Sample data model for inbox notification
fun sampleInboxNotifications(): List<NotificationItem> {
    return listOf(
        NotificationItem(R.drawable.image1, "Amaz Majid", "Say hello 👋", "4h"),
        NotificationItem(R.drawable.image2, "Design Fit Tutorials", "Say hello 👋", "14h"),
        NotificationItem(R.drawable.image3, "Invite your friends", "Connect to start chatting", "22h")
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationsInboxScreenPreview() {
    val mockNavController = rememberNavController() // A mock NavController for preview purposes
    SearchUI(navController = mockNavController) // Directly preview the SearchUI
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedScreenPins(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        topBar = {
            SavedPinsTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)
        }
    ) {
        SavedBoardsContent()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedPinsTopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Profile Icon and Tabs (Pins, Boards)
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // User avatar or profile icon
            Image(
                painter = painterResource(id = R.drawable.image1), // replace with actual image resource
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController.navigate("account")
                    }
            )

            Text(
                text = "Pins",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 14.dp, start = 56.dp)
                    .clickable {
                        navController.navigate("pins")
                    }
            )
            Text(
                text = "Boards",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 14.dp, start = 56.dp)
                    .clickable {
                        navController.navigate("boards")
                    }
            )
        }

        // Search Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Search input field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle input change */ },
                label = { Text(text = "Search your Pins") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .width(400.dp)  // Adjusted width
                    .padding(bottom = 16.dp),  // Space below the input field
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenPinsPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    SavedScreenPins(navController = mockNavController)
}

//
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedScreenBoards(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        topBar = {
            SavedBoardsTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)
        }
    ) {
        SavedBoardsContent()
    }
}

@Composable
fun SavedBoardsTopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Profile Icon and Tabs (Pins, Boards)
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // User avatar or profile icon
            Image(
                painter = painterResource(id = R.drawable.image1), // replace with actual image resource
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController.navigate("account")
                    }
            )

            Text(
                text = "Pins",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 14.dp, start = 56.dp)
                    .clickable {
                        navController.navigate("pins")
                    }
            )
            Text(
                text = "Boards",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 14.dp, start = 56.dp)
                    .clickable {
                        navController.navigate("boards")
                    }
            )
        }

        // Search Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Search input field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle input change */ },
                label = { Text(text = "Search your Pins") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .width(400.dp)  // Adjusted width
                    .padding(bottom = 16.dp),  // Space below the input field
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )
        }


    }
}

@Composable
fun SavedBoardsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 160.dp, start = 16.dp, end = 16.dp)
    ) {
        // Sample content
        Text(
            text = "Character design animations",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        // Image and text showing the number of pins and time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image2), // replace with actual image
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.image3),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.image4),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }

        Text(text = "3 Pins", fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenBoardsPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    SavedScreenBoards(navController = mockNavController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)  // Adding the bottom navigation bar
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
// Title section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically  // Center vertically
            ) {
                IconButton(onClick = {
                    // Handle back button action
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                // Spacer to push the title to the center
                Spacer(modifier = Modifier.weight(0.75f))

                // Title at the center
                Text(
                    text = "Your Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Spacer to balance the layout
                Spacer(modifier = Modifier.weight(1f))
            }

            // Profile section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                // Avatar circle with initial letter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF6200EA), CircleShape)
                ) {
                    Text(
                        text = "F",  // Initial letter for avatar
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // User details section
                Column {
                    Text(text = "Farhan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "View profile",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("avatar")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Log out option
            Text(
                text = "Log out",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable {
                        // Handle log out click
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    AccountScreen(navController = mockNavController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AvatarScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        topBar = {
            // Custom top bar implementation using a Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    // Handle back button action
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                // Empty space for the title (as per the image)
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    // Handle share action
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = currentRoute)  // Adding the bottom navigation bar
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Avatar circle with initial letter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF6200EA), CircleShape)
                ) {
                    Text(
                        text = "F",  // Initial letter for avatar
                        color = Color.White,
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User name and details section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Farhan Khan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@farhankhanfk2003",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Followers and following info
                Text(
                    text = "0 followers · 0 following",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button
            Button(
                onClick = {
                    // Handle edit profile action
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Edit profile")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AvatarScreenPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    AvatarScreen(navController = mockNavController)
}