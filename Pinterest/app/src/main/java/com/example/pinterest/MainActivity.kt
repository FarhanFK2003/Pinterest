//package com.example.pinterest
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.pinterest.ui.theme.PinterestTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            PinterestTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PinterestTheme {
//        Greeting("Android")
//    }
//}
//

package com.example.pinterest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.pinterest.ui.theme.PinterestTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PinterestTheme {
                PinterestUI()
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

@Composable
fun HomeScreen() {
    // Main screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "All",
            color = Color.Black,
            modifier = Modifier.padding(vertical = 5.dp)
                .align(Alignment.CenterHorizontally),  // Ensure centered alignment
            fontSize = 22.sp,  // Adjusted font size
        )

        ImagesGrid() // The grid of images
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
            .size(150.dp) // Size of each image card
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        //elevation = 4.dp
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedUI() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start  // Align items to the start
        ) {
            // Profile picture at the top left
            Image(
                painter = painterResource(id = R.drawable.image1), // Profile picture resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp) // Adjust the size of the profile picture
                    .padding(bottom = 16.dp) // Space below the profile picture
            )

            Text(
                text = "Pins",
                color = Color.Black,
                modifier = Modifier.padding(top = 5.dp)
                    .align(Alignment.CenterHorizontally),  // Ensure centered alignment
                fontSize = 22.sp,  // Adjusted font size
            )

            // Search input field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle input change */ },
                label = { Text(text = "Search") },
                modifier = Modifier
                    .width(300.dp)  // Adjusted width
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
fun SavedScreenPreview() {
    SavedUI()
}