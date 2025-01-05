//@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pinterest

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.pinterest.ui.theme.PinterestTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = ""
)


//class MainActivity : ComponentActivity() {
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        firestore = FirebaseFirestore.getInstance() // Initialize Firestore
//        setContent {
//            PinterestTheme {
//                AppNavigation(auth = FirebaseAuth.getInstance())
//            }
//        }
//    }
//}

//interface FirestoreRepository {
//    fun getImages(
//        onSuccess: (List<Image>) -> Unit,
//        onFailure: (Exception) -> Unit
//    )
//}
//
//// Implementation of the interface
//class FirestoreRepositoryImpl : FirestoreRepository {
//    private val firestore = FirebaseFirestore.getInstance()
//
//    override fun getImages(
//        onSuccess: (List<Image>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        firestore.collection("images").get()
//            .addOnSuccessListener { snapshot ->
//                val images = snapshot.documents.mapNotNull { it.toObject(Image::class.java) }
//                onSuccess(images)
//            }
//            .addOnFailureListener { exception ->
//                onFailure(exception)
//            }
//    }
//}

interface DatabaseRepository {
    fun getImages(
        onSuccess: (List<Image>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun addImage(
        image: Image,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}


class RealtimeDatabaseRepository : DatabaseRepository {
    private val database = FirebaseDatabase.getInstance().reference

    override fun getImages(
        onSuccess: (List<Image>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("RealtimeDatabaseRepository", "Fetching images from Realtime Database.")
        database.child("images").get()
            .addOnSuccessListener { snapshot ->
                val images = snapshot.children.mapNotNull { it.getValue(Image::class.java) }
                onSuccess(images)
                Log.d("RealtimeDatabaseRepository", "Fetched ${images.size} images successfully.")
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
                Log.e("RealtimeDatabaseRepository", "Error fetching images from database: ${exception.message}", exception)
            }
    }

    override fun addImage(
        image: Image,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val key = database.child("images").push().key
        if (key == null) {
            onFailure(Exception("Key generation failed"))
            Log.e("RealtimeDatabaseRepository", "Failed to generate key for new image.")
            return
        }

        database.child("images").child(key).setValue(image)
            .addOnSuccessListener {
                onSuccess()
                Log.d("RealtimeDatabaseRepository", "Image added successfully: $image")
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
                Log.e("RealtimeDatabaseRepository", "Failed to add image: ${exception.message}", exception)
            }
    }
}

data class Image(
    val id: String = "",
    val image_url: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val user_id: String = ""
)

fun uploadImage(
    uri: Uri,
    metadata: Image,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${uri.lastPathSegment}")

    // Upload the file to Firebase Storage
    storageRef.putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
            // Get the download URL after successful upload
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val updatedImage = metadata.copy(image_url = downloadUri.toString())

                // Add image metadata to Firestore
                FirebaseFirestore.getInstance()
                    .collection("images")
                    .add(updatedImage)
                    .addOnSuccessListener {
                        onSuccess() // Callback for success
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception) // Handle Firestore failure
                    }
            }.addOnFailureListener { exception ->
                onFailure(exception) // Handle failure to retrieve download URL
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception) // Handle failure to upload to Firebase Storage
        }
}

//class RealtimeDatabaseRepository {
//    private val database = FirebaseDatabase.getInstance().reference
//
//    fun getImages(
//        onSuccess: (List<Image>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        database.child("images").get()
//            .addOnSuccessListener { snapshot ->
//                val images = snapshot.children.mapNotNull { it.getValue(Image::class.java) }
//                onSuccess(images)
//            }
//            .addOnFailureListener { exception ->
//                onFailure(exception)
//            }
//    }
//
//    fun saveImage(
//        image: Image,
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        val imageId = database.child("images").push().key
//        if (imageId != null) {
//            val imageWithId = image.copy(id = imageId)
//            database.child("images").child(imageId).setValue(imageWithId)
//                .addOnSuccessListener { onSuccess() }
//                .addOnFailureListener { onFailure(it) }
//        } else {
//            onFailure(Exception("Failed to generate image ID"))
//        }
//    }
//}


// Original
//class MainActivity : ComponentActivity() {
//    private lateinit var auth: FirebaseAuth
//    private lateinit var repository: RealtimeDatabaseRepository
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize FirebaseAuth
//        auth = FirebaseAuth.getInstance()
//
//        // Use the RealtimeDatabaseRepository
//        repository = RealtimeDatabaseRepository()
//
//        setContent {
//            PinterestTheme {
//                AppNavigation(auth = auth, repository = repository)
//            }
//        }
//    }
//}


//class MainActivity : ComponentActivity() {
//    private lateinit var auth: FirebaseAuth
//    private lateinit var repository: FirestoreRepository
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize FirebaseAuth
//        auth = FirebaseAuth.getInstance()
//
//        // Use the implementation of FirestoreRepository
//        repository = FirestoreRepositoryImpl()
//
//        setContent {
//            PinterestTheme {
//                AppNavigation(auth = auth, repository = repository)
//            }
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: RealtimeDatabaseRepository
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth and Realtime Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Use RealtimeDatabaseRepository
        repository = RealtimeDatabaseRepository()

        setContent {
            PinterestTheme {
                AppNavigation(auth = auth, repository = repository, database = database)
            }
        }
    }
}




//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun PinterestUI() {
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Pinterest logo at the top
//            Image(
//                painter = painterResource(id = R.drawable.pinterest_logo), // Use the Pinterest icon
//                contentDescription = "Pinterest Logo",
//                modifier = Modifier
//                    .size(150.dp)  // Adjusted logo size
//                    .padding(bottom = 32.dp),  // More space below the logo
//                //contentScale = ContentScale.Crop
//            )
//
//            // Welcome text
//            Text(
//                text = "Welcome to Pinterest",
//                fontSize = 22.sp,  // Adjusted font size
//                modifier = Modifier.padding(bottom = 24.dp)  // Increased space below the text
//            )
//
//            // Email input field
//            OutlinedTextField(
//                value = "",
//                onValueChange = { /* Handle input change */ },
//                label = { Text(text = "Email address") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)  // Adjusted width
//                    .padding(bottom = 24.dp),  // Increased space below the input field
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Red,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Red,
//                    errorCursorColor = Color.Red
//                )
//            )
//
//            // Continue button
//            Button(
//                onClick = { /* No logic needed */ },
//                modifier = Modifier
//                    .width(300.dp)  // Adjusted width
//                    .padding(bottom = 16.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text(text = "Continue", color = Color.White)
//            }
//
//            // Continue with Facebook button
//            Button(
//                onClick = { /* No logic needed */ },
//                modifier = Modifier
//                    .width(300.dp)  // Adjusted width
//                    .padding(top = 16.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
//            ) {
//                Text(text = "Continue with Facebook", color = Color.White)
//            }
//
//            // Continue with Google button
//            Button(
//                onClick = { /* No logic needed */ },
//                modifier = Modifier
//                    .width(300.dp),  // Adjusted width
//                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
//                shape = CircleShape,
//                border = BorderStroke(1.dp, Color.Gray)
//            ) {
//                Text(text = "Continue with Google", color = Color.Black)
//            }
//
//            Spacer(modifier = Modifier.height(48.dp))  // Increased space before privacy text
//
//            // Privacy policy and terms
//            Text(
//                text = "By continuing, you agree to Pinterest's Terms of Service and acknowledge that you've read our Privacy Policy.",
//                fontSize = 12.sp,
//                color = Color.Black,
//                modifier = Modifier
//                    .padding(horizontal = 32.dp)  // Adjusted padding for alignment
//                    .align(Alignment.CenterHorizontally),  // Ensure centered alignment
//                lineHeight = 16.sp
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PinterestUIPreview() {
//    PinterestTheme {
//        PinterestUI()
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PinterestUI(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") } // State for the email input
    var errorMessage by remember { mutableStateOf("") } // State for validation errors

    // Skip Firebase operations in preview mode
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current // Define the context here

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
                painter = painterResource(id = R.drawable.pinterest_logo),
                contentDescription = "Pinterest Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp)
            )

            // Welcome text
            Text(
                text = "Welcome to Pinterest",
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = "" // Clear error on text change
                },
                label = { Text(text = "Email address") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                ),
                isError = errorMessage.isNotEmpty() // Highlight input box on error
            )

            // Show error message if any
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Continue button
            Button(
                onClick = {
                    if(!isPreview)
                    {
                        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            // Firebase functionality to validate email
                            auth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val signInMethods = task.result?.signInMethods
                                        if (signInMethods.isNullOrEmpty()) {
                                            // No account exists with this email, navigate to login or create flow
                                            navController.navigate("login")
                                            Toast.makeText(context, "Email is valid! Navigate to Login.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Account already exists
                                            Toast.makeText(context, "Email already exists, proceed to Login.", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login")
                                        }
                                    } else {
                                        errorMessage = "Failed to validate email: ${task.exception?.message}"
                                    }
                                }
                        } else {
                            // Show error if email is invalid
                            errorMessage = "Please enter a valid email address."
                        }
                    }

                },
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Continue", color = Color.White)
            }

            // Continue with Facebook button
            Button(
                onClick = { /* Facebook login logic */ },
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Text(text = "Continue with Facebook", color = Color.White)
            }

            // Continue with Google button
            Button(
                onClick = { /* Google login logic */ },
                modifier = Modifier
                    .width(300.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(text = "Continue with Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Privacy policy and terms
            Text(
                text = "By continuing, you agree to Pinterest's Terms of Service and acknowledge that you've read our Privacy Policy.",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .align(Alignment.CenterHorizontally),
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinterestUIPreview() {
    PinterestTheme {
        // Use a mock NavController for the preview
        val mockNavController = rememberNavController()

        // Provide a fake FirebaseAuth for preview
        val fakeAuth = FirebaseAuth.getInstance()

        // Pass mock dependencies to PinterestUI
        PinterestUI(
            navController = mockNavController,
            auth = fakeAuth // This won't actually call Firebase during preview
        )
    }
}

////
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun LoginScreen() {
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Log in",
//                color = Color.Black,
//                modifier = Modifier.padding(vertical = 5.dp)
//                    .align(Alignment.CenterHorizontally),  // Ensure centered alignment
//                fontSize = 22.sp,  // Adjusted font size
//            )
//            // Facebook Login Button
//            Button(
//                onClick = { /* Handle Facebook login */ },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
//            ) {
////                Image(
////                    painter = painterResource(id = R.drawable.facebook_icon),  // Facebook icon resource
////                    contentDescription = "Facebook Logo",
////                    modifier = Modifier.size(24.dp)
////                )
////                Spacer(modifier = Modifier.width(8.dp))
//                Text(text = "Continue with Facebook", color = Color.White)
//            }
//
//            // Google Login Button
//            Button(
//                onClick = { /* Handle Google login */ },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)), // White background
//                border = BorderStroke(1.dp, Color.Gray)
//            ) {
////                Image(
////                    painter = painterResource(id = R.drawable.google_icon),  // Google icon resource
////                    contentDescription = "Google Logo",
////                    modifier = Modifier.size(24.dp)
////                )
////                Spacer(modifier = Modifier.width(8.dp))
//                Text(text = "Continue with Google", color = Color.Black)
//            }
//
//            // OR Divider Text
//            Text(
//                text = "Or",
//                color = Color.Gray,
//                modifier = Modifier.padding(vertical = 16.dp)
//            )
//
//            // Email Input Field
//            OutlinedTextField(
//                value = "",
//                onValueChange = { /* Handle email input */ },
//                label = { Text(text = "Email Address") },  // Pre-filled with email
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Gray,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Gray
//                ),
////                leadingIcon = {
////                    Icon(
////                        painter = painterResource(id = R.drawable.email_icon), // Optional email icon
////                        contentDescription = "Email Icon"
////                    )
////                }
//            )
//
//            // Password Input Field
//            OutlinedTextField(
//                value = "",
//                onValueChange = { /* Handle password input */ },
//                label = { Text(text = "Enter your password") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Gray,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Gray
//                ),
////                leadingIcon = {
////                    Icon(
////                        painter = painterResource(id = R.drawable.password_icon), // Optional password icon
////                        contentDescription = "Password Icon"
////                    )
////                },
////                trailingIcon = {
////                    Icon(
////                        painter = painterResource(id = R.drawable.visibility_icon),  // Optional eye/visibility icon
////                        contentDescription = "Toggle Password Visibility"
////                    )
////                }
//            )
//
//            // Log In Button
//            Button(
//                onClick = { /* Handle login */ },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))  // Red button background
//            ) {
//                Text(text = "Log In", color = Color.White)
//            }
//
//            // Forgot Password
//            Text(
//                text = "Forgot password?",
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//        }
//    }
//}


// Original Login
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf("") }
//    var showResetDialog by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Log in",
//                color = Color.Black,
//                modifier = Modifier.padding(vertical = 5.dp),
//                fontSize = 22.sp
//            )
//
//            // Facebook Login Button
//            Button(
//                onClick = { /* Implement Facebook login */ },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
//            ) {
//                Text(text = "Continue with Facebook", color = Color.White)
//            }
//
//            // Google Login Button
//            Button(
//                onClick = { /* Implement Google login */ },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
//                border = BorderStroke(1.dp, Color.Gray)
//            ) {
//                Text(text = "Continue with Google", color = Color.Black)
//            }
//
//            // OR Divider Text
//            Text(
//                text = "Or",
//                color = Color.Gray,
//                modifier = Modifier.padding(vertical = 16.dp)
//            )
//
//            // Email Input Field
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text(text = "Email Address") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Gray,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Gray
//                )
//            )
//
//            // Password Input Field
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text(text = "Enter your password") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Gray,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Gray
//                )
//            )
//
//            // Show Error Message
//            if (errorMessage.isNotEmpty()) {
//                Text(
//                    text = errorMessage,
//                    color = Color.Red,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//            }
//
//            // Log In Button
//            Button(
//                onClick = {
//                    if (email.isNotEmpty() && password.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                        // Check if user exists and log in or create a new user
//                        auth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
//                                    navController.navigate("home")
//                                } else {
//                                    // If user does not exist, create a new user
//                                    auth.createUserWithEmailAndPassword(email, password)
//                                        .addOnCompleteListener { createTask ->
//                                            if (createTask.isSuccessful) {
//                                                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
//                                                navController.navigate("home")
//                                            } else {
//                                                errorMessage = createTask.exception?.message ?: "Failed to create account."
//                                            }
//                                        }
//                                }
//                            }
//                    } else {
//                        errorMessage = "Please enter valid email and password."
//                    }
//                },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
//            ) {
//                Text(text = "Log In", color = Color.White)
//            }
//
//            // Forgot Password
//            Text(
//                text = "Forgot password?",
//                color = Color.Gray,
//                modifier = Modifier
//                    .padding(top = 16.dp)
//                    .clickable {
//                        showResetDialog = true
//                    }
//            )
//        }
//    }
//
//    // Show Reset Password Dialog
//    if (showResetDialog) {
//        AlertDialog(
//            onDismissRequest = { showResetDialog = false },
//            title = { Text("Reset Password") },
//            text = {
//                Column {
//                    Text("Enter your email to reset your password.")
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        label = { Text(text = "Email Address") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
//                    )
//                }
//            },
//            confirmButton = {
//                Button(onClick = {
//                    if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                        auth.sendPasswordResetEmail(email)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
//                                    showResetDialog = false
//                                } else {
//                                    Toast.makeText(context, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                    } else {
//                        Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
//                    }
//                }) {
//                    Text("Send")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { showResetDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    PinterestTheme {
//        LoginScreen()
//    }
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(30.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "All",
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier
//                    .padding(vertical = 5.dp)
//                    .align(Alignment.CenterHorizontally),
//                fontSize = 22.sp
//            )
//            ImagesGrid(navController) // The grid of images
//        }
//
//        // Use the extracted BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(navController: NavController, repository: FirestoreRepository) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(30.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "All",
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier
//                    .padding(vertical = 5.dp)
//                    .align(Alignment.CenterHorizontally),
//                fontSize = 22.sp
//            )
//            ImagesGrid(navController, repository) // The grid of images
//        }
//
//        // Use the extracted BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, repository: DatabaseRepository) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Remember bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = {
                    showBottomSheet = true // Trigger bottom sheet visibility
                }
            )
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
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 22.sp
            )
            ImagesGrid(navController, repository) // The grid of images
        }

        // Use the extracted BottomSheetContent composable
        BottomSheetContent(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}


@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?, onCreateClick: () -> Unit) {
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
            onClick = {
                onCreateClick() // Call the lambda to trigger bottom sheet
            }
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

//@Composable
//fun ImagesGrid(navController: NavController) {
//    // List of image resources
//    val images = listOf(
//        R.drawable.image1,
//        R.drawable.image2,
//        R.drawable.image3,
//        R.drawable.image4,
//        R.drawable.image1,
//        R.drawable.image2,
//        R.drawable.image3,
//        R.drawable.image4
//    )
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2), // 2 columns in the grid
//        contentPadding = PaddingValues(16.dp), // Padding around the grid
//        verticalArrangement = Arrangement.spacedBy(16.dp), // Space between rows
//        horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Render each image in the grid
//        items(images) { imageRes ->
//            ImageCard(imageRes, navController)
//        }
//    }
//}



//@Composable
//fun ImageCard(imageRes: Int, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp) // Image size
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Image(
//            painter = painterResource(id = imageRes),
//            contentDescription = "Image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    // Navigate to the edit screen on click
//                    navController.navigate("imageDetail")
//                }
//        )
//    }
//}

//@Composable
//fun ImagesGrid(navController: NavController, repository: FirestoreRepository) {
//    var images by remember { mutableStateOf<List<Image>>(emptyList()) }
//    var errorMessage by remember { mutableStateOf("") }
//
//    // Fetch images on load
//    LaunchedEffect(Unit) {
//        repository.getImages(
//            onSuccess = { images = it },
//            onFailure = { errorMessage = it.message ?: "Error loading images" }
//        )
//    }
//
//    if (errorMessage.isNotEmpty()) {
//        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
//    } else {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(images) { image ->
//                ImageCard(image, navController)
//            }
//        }
//    }
//}
//
//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Image(
//            painter = rememberImagePainter(
//                data = image.image_url,
//                builder = {
//                    placeholder(R.drawable.placeholder) // Valid drawable resource
//                    error(R.drawable.error) // Valid drawable resource
//                }
//            ),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    navController.navigate("imageDetail/${Uri.encode(image.image_url)}")
//                }
//        )
//    }
//}

//@Composable
//fun ImagesGrid(navController: NavController, repository: DatabaseRepository) {
//    var images by remember { mutableStateOf<List<Image>>(emptyList()) }
//    var errorMessage by remember { mutableStateOf("") }
//
//    // Fetch images on load
//    LaunchedEffect(Unit) {
//        repository.getImages(
//            onSuccess = { images = it },
//            onFailure = { errorMessage = it.message ?: "Error loading images" }
//        )
//    }
//
//    if (errorMessage.isNotEmpty()) {
//        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
//    } else {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(images) { image ->
//                ImageCard(image, navController)
//            }
//        }
//    }
//}

@Composable
fun ImagesGrid(navController: NavController, repository: DatabaseRepository) {
    var images by remember { mutableStateOf<List<Image>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetch images on load
    LaunchedEffect(Unit) {
        repository.getImages(
            onSuccess = { fetchedImages ->
                images = fetchedImages
                Log.d("ImagesGrid", "Successfully fetched ${fetchedImages.size} images from the database.")
            },
            onFailure = { exception ->
                errorMessage = exception.message ?: "Error loading images"
                Log.e("ImagesGrid", "Failed to fetch images: $errorMessage", exception)
            }
        )
    }

    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(images) { image ->
                ImageCard(image, navController)
            }
        }
    }
}

//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
////        Image(
////            painter = rememberImagePainter(
////                data = image.image_url,
////                builder = {
////                    placeholder(R.drawable.placeholder) // Valid drawable resource
////                    error(R.drawable.error) // Valid drawable resource
////                }
////            ),
////            contentDescription = image.title,
////            contentScale = ContentScale.Crop,
////            modifier = Modifier
////                .fillMaxSize()
////                .clickable {
////                    navController.navigate("imageDetail/${Uri.encode(image.image_url)}")
////                }
////        )
//        Image(
//
//            painter = rememberAsyncImagePainter(
//                model = image.image_url,
//                error = painterResource(R.drawable.error), // Placeholder in case of an error
//                placeholder = painterResource(R.drawable.placeholder) // Placeholder during loading
//            ),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    navController.navigate("imageDetail/${Uri.encode(image.image_url)}")
//                }
//        )
//    }
//}

// Original
//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        // Log the URL being loaded
//        Log.d("ImageCard", "Attempting to load image from URL: ${image.image_url}")
//
//        Image(
//            painter = rememberAsyncImagePainter(
//                model = image.image_url,
//                placeholder = painterResource(R.drawable.placeholder), // Placeholder image resource
//                error = painterResource(R.drawable.error), // Error image resource
//                onError = { error ->
//                    // Log the error if the image fails to load
//                    Log.e("ImageCard", "Image loading failed for URL: ${image.image_url}, Error: ${error.result.throwable}")
//                }
//            ),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    Log.d("ImageCard", "Image clicked: ${image.title}, URL: ${image.image_url}")
//                    navController.navigate("imageDetail/${Uri.encode(image.image_url)}")
//                }
//        )
//    }
//}


//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(
//                model = image.image_url,
//                placeholder = painterResource(R.drawable.placeholder), // Placeholder image resource
//                error = painterResource(R.drawable.error) // Error image resource
//            ),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    // Navigate to the Image Detail Screen with the encoded URL
//                    //navController.navigate("imageDetail/${Uri.encode(image.image_url)}")
//                    navController.navigate(
//                        "imageDetail/${Uri.encode(Gson().toJson(image))}"
//                    )
//                }
//        )
//    }
//}

//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(
//                model = image.image_url,
//                placeholder = painterResource(R.drawable.placeholder),
//                error = painterResource(R.drawable.error)
//            ),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable {
//                    // Navigate to ImageDetailScreen with the image ID
//                    navController.navigate("imageDetail/${image.id}")
//                }
//        )
//    }
//}

//@Composable
//fun ImageCard(image: Image, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .clickable {
//                // Navigate to the ImageDetailScreen and pass the image data as JSON
//                val imageJson = Uri.encode(Gson().toJson(image))
//                navController.navigate("imageDetail/$imageJson")
//            }
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(model = image.image_url),
//            contentDescription = image.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}

@Composable
fun ImageCard(image: Image, navController: NavController) {
    val gson = Gson() // Initialize Gson instance

    Card(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                // Serialize the image object to a JSON string
                val imageJson = gson.toJson(image)
                // Navigate to ImageDetailScreen, passing the serialized JSON string
                navController.navigate("imageDetail/${Uri.encode(imageJson)}")
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = image.image_url,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.error)
            ),
            contentDescription = image.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}



// Original
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchUI(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start
//        ) {
//            // Search input
//            OutlinedTextField(
//                value = "",
//                onValueChange = { /* Handle input change */ },
//                label = { Text(text = "Search for Ideas") },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search Icon"
//                    )
//                },
//                modifier = Modifier
//                    .width(400.dp)
//                    .padding(bottom = 16.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Red,
//                    unfocusedBorderColor = Color.Gray,
//                    cursorColor = Color.Red
//                )
//            )
//        }
//
//        // Use the extracted BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchUI(navController: NavController, database: DatabaseReference) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val allImages = remember { mutableStateOf<List<Image>>(emptyList()) }
    val displayedImages = remember { mutableStateOf<List<Image>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Remember bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Fetch images from Firebase
    LaunchedEffect(Unit) {
        database.child("images").get()
            .addOnSuccessListener { snapshot ->
                val imagesList = mutableListOf<Image>()
                snapshot.children.forEach { child ->
                    val image = child.getValue(Image::class.java)
                    if (image != null) {
                        imagesList.add(image)
                    }
                }
                allImages.value = imagesList
                displayedImages.value = imagesList // Initially show all images
            }
            .addOnFailureListener {
                errorMessage = "Failed to load images."
            }
    }

    // Update displayed images when search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            displayedImages.value = allImages.value
        } else {
            displayedImages.value = allImages.value.filter { image ->
                image.title.contains(searchQuery, ignoreCase = true) ||
                        image.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = {
                    showBottomSheet = true // Trigger bottom sheet visibility
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> searchQuery = query },
                label = { Text(text = "Search for Ideas") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )

            // Display search results or error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (displayedImages.value.isEmpty()) {
                Text(
                    text = "No results found.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedImages.value) { image ->
                        ImageCard(image = image, navController = navController)
                    }
                }
            }
        }

        // Use the extracted BottomSheetContent composable
        BottomSheetContent(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}





//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    // NavHost defines the navigation graph
//    NavHost(navController = navController, startDestination = "home") {
//        // Home screen route
//        composable("home") {
//            HomeScreen(navController)  // Pass navController to navigate to other screens
//        }
//        // Search screen route
//        composable("search") {
//            SearchUI(navController)  // Search screen
//        }
//        // Notifications screen route
//        composable("notifications") {
//            NotificationsScreen(navController)
//        }
//        // Saved screen route
//        composable("saved") {
//            SavedScreenPins(navController) // Navigating to SavedScreenPins
//        }
//
//        composable("updates") {
//            NotificationsScreen(navController) // Updates screen
//        }
//
//        composable("inbox") {
//            NotificationsInboxScreen(navController) // Updates screen
//        }
//
//        composable("pins") {
//            SavedScreenPins(navController)
//        }
//
//        composable("boards") {
//            SavedScreenBoards(navController)
//        }
//
//        composable("account") {
//            AccountScreen(navController)
//        }
//
//        composable("avatar") {
//            AvatarScreen(navController)
//        }
//
//        composable("profile") {
//            EditProfileScreen(navController)
//        }
//
//        composable("imageDetail") {
//            ImageDetailScreen(navController)
//        }
//    }
//}

//@Composable
//fun AppNavigation(auth: FirebaseAuth, repository: FirestoreRepository) {
//    val navController = rememberNavController()
//
//    // Define the navigation graph
//    NavHost(navController = navController, startDestination = "pinterestUI") {
//        // Pinterest UI screen
//        composable("pinterestUI") {
//            PinterestUI(navController = navController, auth = auth)
//        }
//
//        // Login screen route
//        composable("login") {
//            LoginScreen(navController = navController, auth = auth)
//        }
//
//        // Home screen route
//        composable("home") {
//            HomeScreen(navController = navController, repository = repository) // Pass NavController for navigation
//        }
//
//        // Additional screens
//        composable("search") {
//            SearchUI(navController)
//        }
//        composable("notifications") {
//            NotificationsScreen(navController)
//        }
//        composable("saved") {
//            SavedScreenPins(navController)
//        }
//        composable("updates") {
//            NotificationsScreen(navController)
//        }
//        composable("inbox") {
//            NotificationsInboxScreen(navController)
//        }
//        composable("pins") {
//            SavedScreenPins(navController)
//        }
//        composable("boards") {
//            SavedScreenBoards(navController)
//        }
//        composable("account") {
//            AccountScreen(navController = navController, auth = auth)
//        }
//        composable("avatar") {
//            AvatarScreen(navController)
//        }
//        composable("profile") {
//            EditProfileScreen(navController)
//        }
//        composable("imageDetail") {
//            ImageDetailScreen(navController)
//        }
//    }
//}

// Original
//@Composable
//fun AppNavigation(auth: FirebaseAuth, repository: RealtimeDatabaseRepository) {
//    val navController = rememberNavController()
//
//    // Define the navigation graph
////    NavHost(navController = navController, startDestination = "pinterestUI") {
////        // Pinterest UI screen
////        composable("pinterestUI") {
////            PinterestUI(navController = navController, auth = auth)
////        }
//
//    NavHost(navController = navController, startDestination = "welcome") {
//        // Pinterest UI screen
//        composable("welcome") {
//            WelcomeScreen(navController)
//        }
//
//        // Login screen route
//        composable("login") {
//            LoginScreen(navController = navController, auth = auth)
//        }
//
//        // Home screen route
//        composable("home") {
//            HomeScreen(navController = navController, repository = repository) // Pass NavController for navigation
//        }
//
//        // Additional screens
//        composable("search") {
//            SearchUI(navController)
//        }
//        composable("notifications") {
//            NotificationsScreen(navController)
//        }
//        composable("saved") {
//            SavedScreenPins(navController)
//        }
//        composable("updates") {
//            NotificationsScreen(navController)
//        }
//        composable("inbox") {
//            NotificationsInboxScreen(navController)
//        }
//        composable("pins") {
//            SavedScreenPins(navController)
//        }
//        composable("boards") {
//            SavedScreenBoards(navController)
//        }
//        composable("account") {
//            AccountScreen(navController = navController, auth = auth)
//        }
//        composable("avatar") {
//            AvatarScreen(navController)
//        }
//        composable("profile") {
//            EditProfileScreen(navController)
//        }
//        composable("imageDetail") {
//            ImageDetailScreen(navController)
//        }
//
//        composable("signup") {
//            SignupScreen(navController, auth)
//        }
//    }
//}

@Composable
fun AppNavigation(auth: FirebaseAuth, repository: RealtimeDatabaseRepository, database: DatabaseReference) {
    val navController = rememberNavController()

    // Define the navigation graph
//    NavHost(navController = navController, startDestination = "pinterestUI") {
//        // Pinterest UI screen
//        composable("pinterestUI") {
//            PinterestUI(navController = navController, auth = auth)
//        }

    NavHost(navController = navController, startDestination = "welcome") {
        // Pinterest UI screen
        composable("welcome") {
            WelcomeScreen(navController)
        }

        // Login screen route
        composable("login") {
            LoginScreen(navController = navController, auth = auth)
        }

        // Home screen route
        composable("home") {
            HomeScreen(navController = navController, repository = repository) // Pass NavController for navigation
        }

//        // Additional screens
//        composable("search") {
//            SearchUI(navController)
//        }

        // Additional screens
        composable("search") {
            SearchUI(navController = navController, database = database)
        }

        composable("notifications") {
            NotificationsScreen(navController = navController, database = database)
        }
//        composable("saved") {
//            SavedScreenPins(navController)
//        }
        composable("updates") {
            NotificationsScreen(navController = navController, database = database)
        }
        composable("inbox") {
            NotificationsInboxScreen(navController)
        }
//        composable("pins") {
//            SavedScreenPins(navController)
//        }

        composable("saved") {
            SavedScreenPins(navController = navController, auth = auth, database = database)
        }
        composable("pins") {
            SavedScreenPins(navController = navController, auth = auth, database = database)
        }

        composable("boards") {
            SavedScreenBoards(navController = navController, auth = auth, database = database)
        }
        composable("account") {
            AccountScreen(navController = navController, auth = auth)
        }
        composable("avatar") {
            AvatarScreen(navController = navController, auth = auth)
        }
        composable("profile") {
            EditProfileScreen(navController = navController, auth = auth)
        }
        //composable("imageDetail") {
        //    ImageDetailScreen(navController)
        //}
        composable("signup") {
            SignupScreen(navController, auth, database)
        }

//        // Image Detail Screen route with a dynamic argument
////        composable(
////            route = "imageDetail/{image_url}",
////            arguments = listOf(navArgument("image_url") { type = NavType.StringType })
////        ) { backStackEntry ->
////            val imageUrl = backStackEntry.arguments?.getString("image_url")
////            ImageDetailScreen(navController = navController, imageUrl = imageUrl)
////        }

//        composable(
//            "imageDetail/{imageData}",
//            arguments = listOf(
//                navArgument("imageData") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val imageData = backStackEntry.arguments?.getString("imageData") ?: ""
//            val image = Gson().fromJson(imageData, Image::class.java)
//
//            ImageDetailScreen(navController = navController, image = image)
//        }

//        // Route with image ID argument
//        composable("imageDetail/{imageId}") { backStackEntry ->
//            val imageId = backStackEntry.arguments?.getString("imageId") ?: ""
//            ImageDetailScreen(navController = navController, imageId = imageId, database = database)
//        }


        composable(
            "imageDetail/{image}",
            arguments = listOf(navArgument("image") { type = NavType.StringType })
        ) {
            val imageJson = it.arguments?.getString("image")
            if (imageJson != null) {
                val gson = Gson()
                val image = gson.fromJson(imageJson, Image::class.java)
                ImageDetailScreen(navController = navController, image = image, auth = auth, database = database)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    PinterestTheme {
//        val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
//        val mockNavController = rememberNavController() // Mock NavController
//
//        LoginScreen(navController = mockNavController, auth = mockAuth)
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    //val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
//    val mockNavController = rememberNavController() // Mock NavController
//
//    HomeScreen(navController = mockNavController)
//}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController
//
//    // Mock FirestoreRepository
//    val mockRepository = object : FirestoreRepository {
//        override fun getImages(
//            onSuccess: (List<Image>) -> Unit,
//            onFailure: (Exception) -> Unit
//        ) {
//            // Provide mock data for preview
//            onSuccess(
//                listOf(
//                    Image(
//                        id = "1",
//                        image_url = "https://via.placeholder.com/150",
//                        title = "Mock Image 1",
//                        description = "Description for mock image 1",
//                        category = "Category 1",
//                        user_id = "User1"
//                    ),
//                    Image(
//                        id = "2",
//                        image_url = "https://via.placeholder.com/150",
//                        title = "Mock Image 2",
//                        description = "Description for mock image 2",
//                        category = "Category 2",
//                        user_id = "User2"
//                    )
//                )
//            )
//        }
//    }
//
//    HomeScreen(navController = mockNavController, repository = mockRepository)
//}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockNavController = rememberNavController() // Mock NavController

    // Mock DatabaseRepository
    val mockRepository = object : DatabaseRepository {
        override fun getImages(
            onSuccess: (List<Image>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            // Provide mock data for preview
            onSuccess(
                listOf(
                    Image(
                        id = "1",
                        image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png",
                        title = "a",
                        description = "a",
                        category = "a",
                        user_id = "1"
                    ),
                    Image(
                        id = "1",
                        image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1200px-Google_2015_logo.svg.png",
                        title = "a",
                        description = "a",
                        category = "a",
                        user_id = "2"
                    )
                )
            )
        }

        override fun addImage(
            image: Image,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            // Mock implementation for adding an image
            onSuccess()
        }
    }

    HomeScreen(navController = mockNavController, repository = mockRepository)
}



//@Preview(showBackground = true)
//@Composable
//fun SearchScreenPreview() {
//    val mockNavController = rememberNavController() // A mock NavController for preview purposes
//    SearchUI(navController = mockNavController) // Directly preview the SearchUI
//}

// Original
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationsScreen(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            NotificationsTopBar(navController)
//        },
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) {
//        // Added padding for spacing between the top bar and notifications list
//        NotificationsList(
//            notifications = sampleNotifications(),
//            modifier = Modifier
//                .padding(top = 36.dp) // Padding to create space between top bar and list
//        )
//
//        // Use the extracted BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, database: DatabaseReference) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var imageNotifications by remember { mutableStateOf<List<Pair<Image, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetch notifications
    LaunchedEffect(Unit) {
        database.child("images").get()
            .addOnSuccessListener { snapshot ->
                val tempList = mutableListOf<Pair<Image, String>>()
                snapshot.children.forEach { imageSnapshot ->
                    val image = imageSnapshot.getValue(Image::class.java)
                    if (image != null) {
                        database.child("users").child(image.user_id).child("username").get()
                            .addOnSuccessListener { userSnapshot ->
                                val username = userSnapshot.getValue(String::class.java) ?: "Unknown User"
                                tempList.add(Pair(image, username))
                                imageNotifications = tempList
                            }
                            .addOnFailureListener {
                                tempList.add(Pair(image, "Unknown User"))
                                imageNotifications = tempList
                            }
                    }
                }
            }
            .addOnFailureListener {
                errorMessage = "Failed to load notifications."
            }
    }

    Scaffold(
        topBar = {
            NotificationsTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = { showBottomSheet = true }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it) // Handle scaffold padding
        ) {
            // Add space between top bar and notifications grid
            // Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                NotificationsList(
                    notifications = imageNotifications,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        BottomSheetContent(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}

//@Composable
//fun NotificationsList(notifications: List<NotificationItem>, modifier: Modifier = Modifier) {
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        items(notifications) { notification ->
//            NotificationItemRow(notification)
//        }
//    }
//}

//@Composable
//fun NotificationsList(notifications: List<Pair<Image, String>>, modifier: Modifier = Modifier) {
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        items(notifications) { (image) ->
//            NotificationItemRow(image)
//        }
//    }
//}

@Composable
fun NotificationsTopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
fun NotificationsList(
    notifications: List<Pair<Image, String>>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(notifications) { (image) ->
            NotificationItemRow(image)
        }
    }
}


//@Composable
//fun NotificationItemRow(notification: NotificationItem) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Image
//        Image(
//            painter = painterResource(id = notification.imageRes),
//            contentDescription = null,
//            modifier = Modifier.size(64.dp)
//        )
//
//        // Notification Text
//        Column(
//            modifier = Modifier.padding(start = 16.dp)
//        ) {
//            Text(
//                text = notification.title,
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp
//            )
//            Text(
//                text = notification.subtitle,
//                fontSize = 14.sp
//            )
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // Time
//        Text(
//            text = notification.time,
//            fontSize = 12.sp,
//            color = Color.Gray
//        )
//    }
//}

@Composable
fun NotificationItemRow(image: Image) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Load image from URL
        AsyncImage(
            model = image.image_url,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.error)
        )

        // Notification Text
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = image.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Uploaded by ${image.user_id}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// Sample data model for notification
data class NotificationItem(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val time: String
)

//fun sampleNotifications(): List<NotificationItem> {
//    return listOf(
//        NotificationItem(R.drawable.image1, "Comics Art for you", "", "4h"),
//        NotificationItem(R.drawable.image2, "You have a good eye", "", "14h"),
//        NotificationItem(R.drawable.image3, "Inspired by you", "", "18h"),
//        NotificationItem(R.drawable.image4, "Disney Wallpaper for you", "", "22h"),
//        NotificationItem(R.drawable.image1, "You have a good eye", "", "1d"),
//        NotificationItem(R.drawable.image2, "Wallpaper for you", "", "1d")
//    )
//}

//@Preview(showBackground = true)
//@Composable
//fun NotificationsScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    NotificationsScreen(navController = mockNavController)
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsInboxScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Remember bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NotificationsInboxTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = {
                    showBottomSheet = true // Trigger bottom sheet visibility
                }
            )
        }
    ) {
        // Added padding for spacing between the top bar and inbox list
        NotificationsInboxList(
            notifications = sampleInboxNotifications(),
            modifier = Modifier
                .padding(top = 36.dp) // Padding to create space between top bar and list
        )

        // Use the extracted BottomSheetContent composable
        BottomSheetContent(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
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
    NotificationsInboxScreen(navController = mockNavController) // Directly preview the SearchUI
}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SavedScreenPins(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            SavedPinsTopBar(navController)
//        },
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) {
//        // Saved boards content display
//        SavedBoardsContent()
//
//        // Use the extracted BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SavedScreenPins(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
//    val currentUser = auth.currentUser
//    var savedImages by remember { mutableStateOf<List<Image>>(emptyList()) }
//    var errorMessage by remember { mutableStateOf("") }
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    // Fetch saved images from Firebase
//    LaunchedEffect(currentUser) {
//        currentUser?.let { user ->
//            val userId = user.uid
//            database.child("users").child(userId).child("saved_images").get()
//                .addOnSuccessListener { snapshot ->
//                    val imagesList = mutableListOf<Image>()
//                    snapshot.children.forEach { child ->
//                        val image = child.getValue(Image::class.java)
//                        if (image != null) {
//                            imagesList.add(image)
//                        }
//                    }
//                    savedImages = imagesList
//                }
//                .addOnFailureListener {
//                    errorMessage = "Failed to load saved images."
//                }
//        }
//    }
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = { showBottomSheet = true }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Top bar with search bar included in the scrollable content
//            SavedPinsTopBar(navController = navController)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Check if there are any errors to display
//            if (errorMessage.isNotEmpty()) {
//                Text(
//                    text = errorMessage,
//                    color = Color.Red,
//                    modifier = Modifier.padding(16.dp)
//                )
//            } else {
//                // Display saved images in a grid below the search bar
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(savedImages) { image ->
//                        ImageCard(image = image, navController = navController)
//                    }
//                }
//            }
//        }
//    }
//
//    // Use the extracted BottomSheetContent composable
//    BottomSheetContent(
//        sheetState = sheetState,
//        showBottomSheet = showBottomSheet,
//        onDismissRequest = {
//            scope.launch {
//                sheetState.hide()
//            }.invokeOnCompletion {
//                if (!sheetState.isVisible) {
//                    showBottomSheet = false
//                }
//            }
//        }
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedScreenPins(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
    val currentUser = auth.currentUser
    var savedImages by remember { mutableStateOf<List<Image>>(emptyList()) }
    var filteredImages by remember { mutableStateOf<List<Image>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Remember bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Fetch saved images from Firebase
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("saved_images").get()
                .addOnSuccessListener { snapshot ->
                    val imagesList = mutableListOf<Image>()
                    snapshot.children.forEach { child ->
                        val image = child.getValue(Image::class.java)
                        if (image != null) {
                            imagesList.add(image)
                        }
                    }
                    savedImages = imagesList
                    filteredImages = imagesList // Initially show all images
                }
                .addOnFailureListener {
                    errorMessage = "Failed to load saved images."
                }
        }
    }

    // Filter images based on search query
    LaunchedEffect(searchQuery, savedImages) {
        if (searchQuery.isEmpty()) {
            filteredImages = savedImages
        } else {
            filteredImages = savedImages.filter { image ->
                image.title.contains(searchQuery, ignoreCase = true) ||
                        image.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = { showBottomSheet = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top bar with search bar included
            SavedPinsTopBar(navController = navController, auth = auth, database = database)

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> searchQuery = query },
                label = { Text(text = "Search your Pins") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Check if there are any errors to display
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (filteredImages.isEmpty()) {
                Text(
                    text = "No pins found.",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredImages) { image ->
                        ImageCard(image = image, navController = navController)
                    }
                }
            }
        }
    }

    // Use the extracted BottomSheetContent composable
    BottomSheetContent(
        sheetState = sheetState,
        showBottomSheet = showBottomSheet,
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
            }
        }
    )
}


//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SavedPinsTopBar(navController: NavController) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        // Profile Icon and Tabs (Pins, Boards)
//        Row(
//            horizontalArrangement = Arrangement.Start,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            // User avatar or profile icon
//            Image(
//                painter = painterResource(id = R.drawable.image1), // replace with actual image resource
//                contentDescription = "User Avatar",
//                modifier = Modifier
//                    .size(48.dp)
//                    .clickable {
//                        navController.navigate("account")
//                    }
//            )
//
//            Text(
//                text = "Pins",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .padding(top = 14.dp, start = 56.dp)
//                    .clickable {
//                        navController.navigate("pins")
//                    }
//            )
//            Text(
//                text = "Boards",
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .padding(top = 14.dp, start = 56.dp)
//                    .clickable {
//                        navController.navigate("boards")
//                    }
//            )
//        }
//
////        // Search Bar
////        Row(
////            verticalAlignment = Alignment.CenterVertically,
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(top = 16.dp)
////        ) {
////            // Search input field
////            OutlinedTextField(
////                value = "",
////                onValueChange = { /* Handle input change */ },
////                label = { Text(text = "Search your Pins") },
////                leadingIcon = {
////                    Icon(
////                        imageVector = Icons.Default.Search,
////                        contentDescription = "Search Icon"
////                    )
////                },
////                modifier = Modifier
////                    .width(400.dp)  // Adjusted width
////                    .padding(bottom = 16.dp),  // Space below the input field
////                colors = OutlinedTextFieldDefaults.colors(
////                    focusedBorderColor = Color.Red,
////                    unfocusedBorderColor = Color.Gray,
////                    cursorColor = Color.Red
////                )
////            )
////        }
//
//
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedPinsTopBar(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
    val currentUser = auth.currentUser
    var username by remember { mutableStateOf("") }

    // Fetch username from Firebase
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("username").get()
                .addOnSuccessListener { snapshot ->
                    username = snapshot.value as? String ?: "U"
                }
                .addOnFailureListener {
                    Log.e("SavedPinsTopBar", "Failed to fetch username.")
                }
        }
    }

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
            // User avatar with initial letter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6200EA)) // Background color for the circle
                    .clickable {
                        navController.navigate("account")
                    }
            ) {
                Text(
                    text = username.firstOrNull()?.toString()?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

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
    }
}


//@Preview(showBackground = true)
//@Composable
//fun SavedScreenPinsPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    SavedScreenPins(navController = mockNavController)
//}


//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SavedScreenBoards(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            SavedBoardsTopBar(navController)
//        },
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = {
//                    showBottomSheet = true // Trigger bottom sheet visibility
//                }
//            )
//        }
//    ) {
//        // Saved boards content display
//        SavedBoardsContent()
//
//        // Use the BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SavedScreenBoards(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
//    val currentUser = auth.currentUser
//    var savedBoards by remember { mutableStateOf<Map<String, List<Image>>>(emptyMap()) }
//    var errorMessage by remember { mutableStateOf("") }
//    var searchQuery by remember { mutableStateOf("") }
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//
//    // Remember bottom sheet state and coroutine scope
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    // State to control visibility of the bottom sheet
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    // Fetch saved boards from Firebase
//    LaunchedEffect(currentUser) {
//        currentUser?.let { user ->
//            val userId = user.uid
//            database.child("users").child(userId).child("saved_boards").get()
//                .addOnSuccessListener { snapshot ->
//                    val boardsMap = mutableMapOf<String, MutableList<Image>>()
//                    snapshot.children.forEach { categorySnapshot ->
//                        val categoryName = categorySnapshot.key ?: return@forEach
//                        val imagesList = mutableListOf<Image>()
//                        categorySnapshot.children.forEach { imageSnapshot ->
//                            val image = imageSnapshot.getValue(Image::class.java)
//                            if (image != null) {
//                                imagesList.add(image)
//                            }
//                        }
//                        boardsMap[categoryName] = imagesList
//                    }
//                    savedBoards = boardsMap
//                }
//                .addOnFailureListener {
//                    errorMessage = "Failed to load saved boards."
//                }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            SavedBoardsTopBar(navController = navController, auth = auth, database = database)
//        },
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                currentRoute = currentRoute,
//                onCreateClick = { showBottomSheet = true }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Display error message if any
//            if (errorMessage.isNotEmpty()) {
//                Text(
//                    text = errorMessage,
//                    color = Color.Red,
//                    modifier = Modifier.padding(16.dp)
//                )
//            } else {
//                val filteredBoards = if (searchQuery.isEmpty()) {
//                    savedBoards
//                } else {
//                    savedBoards.filterKeys { it.contains(searchQuery, ignoreCase = true) }
//                }
//
//                if (filteredBoards.isEmpty()) {
//                    Text(
//                        text = "No boards found",
//                        fontSize = 18.sp,
//                        modifier = Modifier.padding(16.dp),
//                        color = Color.Gray
//                    )
//                } else {
//                    // Display the filtered boards with images
//                    filteredBoards.forEach { (category, images) ->
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = category,
//                            fontSize = 22.sp,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.padding(start = 16.dp)
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        LazyVerticalGrid(
//                            columns = GridCells.Fixed(2),
//                            contentPadding = PaddingValues(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(16.dp),
//                            horizontalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            items(images) { image ->
//                                ImageCard(image = image, navController = navController)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        // Use the BottomSheetContent composable
//        BottomSheetContent(
//            sheetState = sheetState,
//            showBottomSheet = showBottomSheet,
//            onDismissRequest = {
//                scope.launch {
//                    sheetState.hide()
//                }.invokeOnCompletion {
//                    if (!sheetState.isVisible) {
//                        showBottomSheet = false
//                    }
//                }
//            }
//        )
//    }
//}

fun saveImageToBoard(
    image: Image,
    auth: FirebaseAuth,
    database: DatabaseReference,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val currentUser = auth.currentUser
    currentUser?.let { user ->
        val userId = user.uid
        val category = image.category // Assuming `Image` class has a `category` field
        database.child("users").child(userId).child("saved_boards").child(category).child(image.id)
            .setValue(image)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Unknown error")
            }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreenBoards(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
    val currentUser = auth.currentUser
    var savedBoards by remember { mutableStateOf<Map<String, List<Image>>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Remember bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Fetch saved boards from Firebase
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("saved_boards").get()
                .addOnSuccessListener { snapshot ->
                    val boardsMap = mutableMapOf<String, MutableList<Image>>()
                    snapshot.children.forEach { categorySnapshot ->
                        val categoryName = categorySnapshot.key ?: return@forEach
                        val imagesList = mutableListOf<Image>()
                        categorySnapshot.children.forEach { imageSnapshot ->
                            val image = imageSnapshot.getValue(Image::class.java)
                            if (image != null) {
                                imagesList.add(image)
                            }
                        }
                        boardsMap[categoryName] = imagesList
                    }
                    savedBoards = boardsMap
                }
                .addOnFailureListener {
                    errorMessage = "Failed to load saved boards."
                }
        }
    }

    Scaffold(
        topBar = {
            SavedBoardsTopBar(navController = navController, auth = auth, database = database)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onCreateClick = { showBottomSheet = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(text = "Search your Boards") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                )
            )

            // Display error message if any
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                val filteredBoards = if (searchQuery.isEmpty()) {
                    savedBoards
                } else {
                    savedBoards.filterKeys { it.contains(searchQuery, ignoreCase = true) }
                }

                if (filteredBoards.isEmpty()) {
                    Text(
                        text = "No boards found",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                } else {
                    // Display the filtered boards with images
                    filteredBoards.forEach { (category, images) ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = category,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(images) { image ->
                                ImageCard(image = image, navController = navController)
                            }
                        }
                    }
                }
            }
        }

        // Use the BottomSheetContent composable
        BottomSheetContent(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}



@Composable
fun SavedBoardsTopBar(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
    val currentUser = auth.currentUser
    var userInitial by remember { mutableStateOf("") }

    // Fetch username and extract initial
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("username").get()
                .addOnSuccessListener { snapshot ->
                    val username = snapshot.getValue(String::class.java) ?: ""
                    userInitial = if (username.isNotEmpty()) username.first().uppercase() else ""
                }
                .addOnFailureListener {
                    userInitial = "?"
                }
        }
    }

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
            // Circular avatar icon with initial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6200EE))
                    .clickable {
                        navController.navigate("account")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

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
    }
}

//@Composable
//fun SavedBoardsContent() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 160.dp, start = 16.dp, end = 16.dp)
//    ) {
//        // Sample content
//        Text(
//            text = "Character design animations",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//        )
//
//        // Image and text showing the number of pins and time
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.image2), // replace with actual image
//                contentDescription = null,
//                modifier = Modifier.size(80.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Image(
//                painter = painterResource(id = R.drawable.image3),
//                contentDescription = null,
//                modifier = Modifier.size(80.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Image(
//                painter = painterResource(id = R.drawable.image4),
//                contentDescription = null,
//                modifier = Modifier.size(80.dp)
//            )
//        }
//
//        Text(text = "3 Pins", fontSize = 14.sp)
//    }
//}







//@Preview(showBackground = true)
//@Composable
//fun SavedScreenBoardsPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    SavedScreenBoards(navController = mockNavController)
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun AccountScreen(navController: NavController, auth: FirebaseAuth) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//    val context = LocalContext.current
//    Scaffold(
////        bottomBar = {
////            BottomNavigationBar(navController, currentRoute = currentRoute)  // Adding the bottom navigation bar
////        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Title section
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                verticalAlignment = Alignment.CenterVertically  // Center vertically
//            ) {
//                IconButton(onClick = {
//                    // Handle back button action
//                    navController.popBackStack()
//                }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//
//                // Spacer to push the title to the center
//                Spacer(modifier = Modifier.weight(0.75f))
//
//                // Title at the center
//                Text(
//                    text = "Your Account",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//
//                // Spacer to balance the layout
//                Spacer(modifier = Modifier.weight(1f))
//            }
//
//            // Profile section
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(vertical = 16.dp)
//            ) {
//                // Avatar circle with initial letter
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .size(64.dp)
//                        .background(Color(0xFF6200EA), CircleShape)
//                ) {
//                    Text(
//                        text = "F",  // Initial letter for avatar
//                        color = Color.White,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                // User details section
//                Column {
//                    Text(text = "Farhan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                    Text(
//                        text = "View profile",
//                        color = Color.Gray,
//                        fontSize = 14.sp,
//                        modifier = Modifier.clickable {
//                            navController.navigate("avatar")
//                        }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Log out option
//            Text(
//                text = "Log out",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp)
//                    .clickable {
//                        // Firebase sign-out logic
//                        auth.signOut()
//                        Toast
//                            .makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT)
//                            .show()
//
//                        // Navigate back to the Pinterest UI screen (login/signup screen)
//                        navController.navigate("pinterestUI") {
//                            popUpTo(navController.graph.startDestinationId) {
//                                inclusive = true
//                            }
//                        }
//                    }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun AccountScreenPreview() {
////    val mockNavController = rememberNavController() // Mock NavController for preview purposes
////    AccountScreen(navController = mockNavController)
//    val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
//    val mockNavController = rememberNavController() // Mock NavController
//
//    AccountScreen(navController = mockNavController, auth = mockAuth)
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun AvatarScreen(navController: NavController) {
//    val currentBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//    Scaffold(
//        topBar = {
//            // Custom top bar implementation using a Row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = {
//                    // Handle back button action
//                    navController.popBackStack()
//                }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//
//                // Empty space for the title (as per the image)
//                Spacer(modifier = Modifier.weight(1f))
//
//                IconButton(onClick = {
//                    // Handle share action
//                }) {
//                    Icon(Icons.Default.Share, contentDescription = "Share")
//                }
//            }
//        },
////        bottomBar = {
////            BottomNavigationBar(navController, currentRoute = currentRoute)  // Adding the bottom navigation bar
////        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Profile section
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                // Avatar circle with initial letter
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .background(Color(0xFF6200EA), CircleShape)
//                ) {
//                    Text(
//                        text = "F",  // Initial letter for avatar
//                        color = Color.White,
//                        fontSize = 50.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User name and details section
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = "Farhan Khan",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "@farhankhanfk2003",
//                    color = Color.Gray,
//                    fontSize = 16.sp
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Followers and following info
//                Text(
//                    text = "0 followers · 0 following",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Edit profile button
//            Button(
//                onClick = {
//                    // Navigate to Profile Screen when clicked
//                    navController.navigate("profile")
//                },
//                shape = RoundedCornerShape(50),
//                modifier = Modifier
//                    .fillMaxWidth(0.5f)
//                    .height(48.dp)
//                    .align(Alignment.CenterHorizontally),
//            ) {
//                Text(text = "Edit profile")
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun AvatarScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    AvatarScreen(navController = mockNavController)
//}



// Original
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheetContent(
//    sheetState: SheetState,
//    showBottomSheet: Boolean,
//    onDismissRequest: () -> Unit
//) {
//    if (showBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = onDismissRequest,
//            sheetState = sheetState
//        ) {
//            // Bottom sheet content
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // Row containing cross icon and text
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    // Cross icon button on the left
//                    IconButton(onClick = onDismissRequest) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//
//                    // Text centered in the row
//                    Text(
//                        text = "Start creating now",
//                        style = MaterialTheme.typography.headlineSmall,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(start = 24.dp), // Takes up remaining space to center the text
//                        //textAlign = TextAlign.Center
//                    )
//                }
//
//                // Add Spacer here to shift the content below down
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Content with pin, camera, and board options
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { /* Handle Pin action */ }) {
//                            Icon(Icons.Default.Add, contentDescription = "Pin")
//                        }
//                        Text("Pin")
//                    }
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { /* Handle Camera action */ }) {
//                            Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera")
//                        }
//                        Text("Camera")
//                    }
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { /* Handle Board action */ }) {
//                            Icon(Icons.Default.AddChart, contentDescription = "Board")
//                        }
//                        Text("Board")
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    sheetState: SheetState,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // Remember the image URI to store the captured image
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Create an ActivityResultLauncher for the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                Toast.makeText(context, "Image captured!", Toast.LENGTH_SHORT).show()
                // Handle the captured image URI (e.g., upload to database or display)
                capturedImageUri?.let { uri ->
                    Log.d("Camera", "Captured Image URI: $uri")
                    // Here you can upload the image to Firebase
                }
            } else {
                Toast.makeText(context, "Image capture failed!", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Function to launch the camera
    fun openCamera() {
        val imageFile = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
        capturedImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Ensure this matches your Manifest provider authority
            imageFile
        )

        // Use a local variable to safely launch the camera
        val uri = capturedImageUri
        if (uri != null) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show()
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            // Bottom sheet content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = "Start creating now",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pin, Camera, Board options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { /* Handle Pin action */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Pin")
                        }
                        Text("Pin")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { openCamera() }) { // Call openCamera function here
                            Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera")
                        }
                        Text("Camera")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { /* Handle Board action */ }) {
                            Icon(Icons.Default.AddChart, contentDescription = "Board")
                        }
                        Text("Board")
                    }
                }
            }
        }
    }
}





//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheetContent(
//    sheetState: SheetState,
//    showBottomSheet: Boolean,
//    onDismissRequest: () -> Unit,
//    auth: FirebaseAuth,
//    database: DatabaseReference
//) {
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri: Uri? ->
//            uri?.let {
//                uploadImageToFirebase(uri, auth.currentUser?.uid ?: "")
//            }
//        }
//    )
//
//    if (showBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = onDismissRequest,
//            sheetState = sheetState
//        ) {
//            // Bottom sheet content
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // Row containing cross icon and text
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    // Cross icon button on the left
//                    IconButton(onClick = onDismissRequest) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//
//                    // Text centered in the row
//                    Text(
//                        text = "Start creating now",
//                        style = MaterialTheme.typography.headlineSmall,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(start = 24.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Content with pin, camera, and board options
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { galleryLauncher.launch("image/*") }) {
//                            Icon(Icons.Default.Add, contentDescription = "Pin")
//                        }
//                        Text("Pin")
//                    }
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { /* Handle Camera action */ }) {
//                            Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera")
//                        }
//                        Text("Camera")
//                    }
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        IconButton(onClick = { /* Handle Board action */ }) {
//                            Icon(Icons.Default.AddChart, contentDescription = "Board")
//                        }
//                        Text("Board")
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun uploadImageToFirebase(imageUri: Uri, userId: String) {
//    val storageReference = FirebaseStorage.getInstance().reference
//        .child("images/${UUID.randomUUID()}") // Unique filename for each image
//
//    val databaseReference = FirebaseDatabase.getInstance().reference
//        .child("users").child(userId).child("uploaded_images")
//
//    // Upload the image to Firebase Storage
//    storageReference.putFile(imageUri)
//        .addOnSuccessListener { taskSnapshot ->
//            // Get the download URL of the uploaded image
//            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
//                val imageId = databaseReference.push().key ?: UUID.randomUUID().toString()
//                val image = Image(
//                    id = imageId,
//                    image_url = downloadUrl.toString(),
//                    title = "New Pin",
//                    description = "Uploaded from gallery",
//                    user_id = userId
//                )
//
//                // Save the image metadata to Firebase Realtime Database
//                databaseReference.child(imageId).setValue(image)
//                    .addOnSuccessListener {
//                        Log.d("Upload", "Image uploaded and saved successfully!")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("Upload", "Failed to save image metadata", e)
//                    }
//            }
//        }
//        .addOnFailureListener { e ->
//            Log.e("Upload", "Image upload failed", e)
//        }
//}








//Original
//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImageDetailScreen(navController: NavController) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO: Implement share functionality */ }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//            // Display the main image
//            Image(
//                painter = painterResource(id = R.drawable.image1), // Replace with actual image resource
//                contentDescription = "Main Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User row with image and name
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // User Profile Image
//                Image(
//                    painter = painterResource(id = R.drawable.image1), // Replace with actual profile image resource
//                    contentDescription = "User Profile",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Username
//                Text(text = "Mr Bigtime", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Buttons Row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween // Space between buttons
//            ) {
//                // "View" Button
//                Button(
//                    onClick = { /* TODO: Implement view functionality */ },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
//                    modifier = Modifier.weight(1f) // Adjust button width
//                ) {
//                    Text(
//                        text = "View",
//                        color = Color.Black // Set text color to black
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
//
//                // "Save" Button
//                Button(
//                    onClick = { /* TODO: Implement save functionality */ },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    modifier = Modifier.weight(1f) // Adjust button width
//                ) {
//                    Text(text = "Save")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Comments Section
//            Text(
//                text = "2 comments",
//                modifier = Modifier.padding(horizontal = 16.dp),
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "josian ... view all",
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Like and other actions row (like count, etc.)
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = { /* TODO: Implement like functionality */ }) {
//                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
//                }
//
//                Text(text = "24")
//            }
//        }
//    }
//}

//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImageDetailScreen(navController: NavController) {
//    val imageUrl = navController.currentBackStackEntry
//        ?.arguments?.getString("image_url") // Retrieve the image URL passed as argument
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO: Implement share functionality */ }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//            // Display the main image
//            if (imageUrl != null) {
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = imageUrl,
//                        placeholder = painterResource(id = R.drawable.placeholder),
//                        error = painterResource(id = R.drawable.error)
//                    ),
//                    contentDescription = "Main Image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(500.dp)
//                )
//            } else {
//                Text(
//                    text = "Image not available",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    textAlign = TextAlign.Center,
//                    color = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User row with image and name
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // User Profile Image
//                Image(
//                    painter = painterResource(id = R.drawable.placeholder), // Replace with actual profile image resource
//                    contentDescription = "User Profile",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Username
//                Text(text = "Mr Bigtime", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Buttons Row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween // Space between buttons
//            ) {
//                // "View" Button
//                Button(
//                    onClick = { /* TODO: Implement view functionality */ },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
//                    modifier = Modifier.weight(1f) // Adjust button width
//                ) {
//                    Text(
//                        text = "View",
//                        color = Color.Black // Set text color to black
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
//
//                // "Save" Button
//                Button(
//                    onClick = { /* TODO: Implement save functionality */ },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    modifier = Modifier.weight(1f) // Adjust button width
//                ) {
//                    Text(text = "Save")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Comments Section
//            Text(
//                text = "2 comments",
//                modifier = Modifier.padding(horizontal = 16.dp),
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "josian ... view all",
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Like and other actions row (like count, etc.)
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = { /* TODO: Implement like functionality */ }) {
//                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
//                }
//
//                Text(text = "24")
//            }
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun ImageDetailScreen(navController: NavController, imageUrl: String?) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO: Implement share functionality */ }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//            if (imageUrl != null) {
//                // Display the main image
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = Uri.decode(imageUrl),
//                        placeholder = painterResource(id = R.drawable.placeholder),
//                        error = painterResource(id = R.drawable.error)
//                    ),
//                    contentDescription = "Main Image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(500.dp)
//                )
//            } else {
//                Text(
//                    text = "Image not available",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    textAlign = TextAlign.Center,
//                    color = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User row with image title and description
//            Text(
//                text = "Image Details",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Description of the image goes here...",
//                fontSize = 14.sp,
//                color = Color.Gray,
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//        }
//    }
//}


//Original
//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImageDetailScreen(navController: NavController, image: Image) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO: Implement share functionality */ }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(16.dp)
//        ) {
//            // Display the main image
//            Image(
//                painter = rememberAsyncImagePainter(
//                    model = image.image_url,
//                    placeholder = painterResource(id = R.drawable.placeholder),
//                    error = painterResource(id = R.drawable.error)
//                ),
//                contentDescription = image.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Title
//            Text(
//                text = image.title,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Description
//            Text(
//                text = image.description,
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User ID
//            Text(
//                text = "Uploaded by User ID: ${image.user_id}",
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
//
//            // Save button at the bottom center
//            Button(
//                onClick = {
////                    // Save functionality: Add image to the "saved" list in Firebase
////                    database.child("saved_images").child(image.id).setValue(image)
////                        .addOnSuccessListener {
////                            Toast.makeText(navController.context, "Image saved!", Toast.LENGTH_SHORT).show()
////                        }
////                        .addOnFailureListener {
////                            Toast.makeText(navController.context, "Failed to save image.", Toast.LENGTH_SHORT).show()
////                        }
//                },
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally) // Align the button at the bottom center
//                    .padding(bottom = 16.dp)
//                    .fillMaxWidth(0.5f) // Button width: 50% of the screen width
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text(text = "Save", color = Color.White)
//            }
//        }
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun ImageDetailScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    ImageDetailScreen(navController = mockNavController)
//}

//@Preview(showBackground = true)
//@Composable
//fun ImageDetailScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    val sampleImageUrl = "R.drawable.placeholder" // Sample image URL for preview
//    ImageDetailScreen(navController = mockNavController, imageUrl = sampleImageUrl)
//}

//@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImageDetailScreen(navController: NavController, image: Image, auth: FirebaseAuth, database: DatabaseReference) {
//    val currentUser = auth.currentUser
//    var isSaved by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO: Implement share functionality */ }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(16.dp)
//        ) {
//            // Display the main image
//            Image(
//                painter = rememberAsyncImagePainter(
//                    model = image.image_url,
//                    placeholder = painterResource(id = R.drawable.placeholder),
//                    error = painterResource(id = R.drawable.error)
//                ),
//                contentDescription = image.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Title
//            Text(
//                text = image.title,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Description
//            Text(
//                text = image.description,
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // User ID
//            Text(
//                text = "Uploaded by User ID: ${image.user_id}",
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Save button at the bottom center
//            Button(
//                onClick = {
//                    currentUser?.let { user ->
//                        val userId = user.uid
//                        val savedImageRef = database.child("users").child(userId).child("saved_images").child(image.id)
//
//                        // Save the image in the user's saved_images node
//                        savedImageRef.setValue(image)
//                            .addOnSuccessListener {
//                                isSaved = true
//                                Toast.makeText(navController.context, "Image saved!", Toast.LENGTH_SHORT).show()
//                            }
//                            .addOnFailureListener {
//                                errorMessage = "Failed to save image."
//                                Toast.makeText(navController.context, errorMessage, Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                },
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(bottom = 16.dp)
//                    .fillMaxWidth(0.5f)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text(text = if (isSaved) "Saved" else "Save", color = Color.White)
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageDetailScreen(navController: NavController, image: Image, auth: FirebaseAuth, database: DatabaseReference) {
    val currentUser = auth.currentUser
    var isSaved by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Check if the image is already saved when the screen loads
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("saved_images").child(image.id).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        isSaved = true // Mark the image as saved if it exists in Firebase
                    }
                }
                .addOnFailureListener {
                    errorMessage = "Failed to check saved status."
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp)
//                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Image Details",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Display the main image
            Image(
                painter = rememberAsyncImagePainter(
                    model = image.image_url,
                    placeholder = painterResource(id = R.drawable.placeholder),
                    error = painterResource(id = R.drawable.error)
                ),
                contentDescription = image.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = image.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = image.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User ID
            Text(
                text = "Uploaded by User ID: ${image.user_id}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    currentUser?.let { user ->
                        val userId = user.uid
                        database.child("users").child(userId).child("saved_images").child(image.id).setValue(image)
                            .addOnSuccessListener {
                                isSaved = true // Update the button state to "Saved"
                                Toast.makeText(navController.context, "Image saved!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(navController.context, "Failed to save image.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    saveImageToBoard(
                        image = image,
                        auth = auth,
                        database = database,
                        onSuccess = {
                            Toast.makeText(navController.context, "Image saved to board!", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(navController.context, "Failed to save image: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                enabled = !isSaved, // Disable the button if the image is already saved
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) Color.Gray else Color.Red
                )
            ) {
                Text(text = if (isSaved) "Saved" else "Save", color = Color.White)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage, color = Color.Red)
            }
        }
    }
}



//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun EditProfileScreen(navController: NavController) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Edit Profile",
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 12.dp)
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    Button(
//                        onClick = { /* TODO: Implement done functionality */ },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                        modifier = Modifier.padding(end = 8.dp) // Padding to adjust positioning
//                    ) {
//                        Text("Done", color = Color.White)
//                    }
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Space between the TopAppBar and the content
//            Spacer(modifier = Modifier.height(50.dp))
//
//            // Display helper text above the avatar
//            Text(
//                text = "Keep your personal details private.\nInformation you add here is visible to anyone who can view your profile.",
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Profile Picture (Avatar)
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFF6200EA))
//            ) {
//                Text(text = "F", fontSize = 48.sp, color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp)) // Space between Avatar and Edit button
//
//            // Edit Button (Below Avatar Image)
//            Button(
//                onClick = { /* TODO: Implement edit profile picture */ },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
//            ) {
//                Text(text = "Edit", color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(24.dp)) // Spacing between the button and input fields
//
//            // First Name Input
//            OutlinedTextField(
//                value = "Farhan",
//                onValueChange = { /* TODO: Handle first name change */ },
//                label = { Text("First name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Last Name Input
//            OutlinedTextField(
//                value = "Khan",
//                onValueChange = { /* TODO: Handle last name change */ },
//                label = { Text("Last name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Username Input
//            OutlinedTextField(
//                value = "farhankhanfk2003",
//                onValueChange = { /* TODO: Handle username change */ },
//                label = { Text("Username") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun EditProfileScreenPreview() {
//    val mockNavController = rememberNavController() // Mock NavController for preview purposes
//    EditProfileScreen(navController = mockNavController)
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(navController: NavController) {
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
                painter = painterResource(id = R.drawable.pinterest_logo),
                contentDescription = "Pinterest Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp)
            )

            // Welcome text
            Text(
                text = "Welcome to Pinterest",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp),
                color = Color.Black
            )

            // Login button
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Login", color = Color.White)
            }

            // Signup button
            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.width(300.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)) // Blue color
            ) {
                Text(text = "Signup", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val mockNavController = rememberNavController()
    PinterestTheme {
    WelcomeScreen(navController = mockNavController)
        }
}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SignupScreen(navController: NavController, auth: FirebaseAuth) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf("") }
//    val context = LocalContext.current
//
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Signup Heading
//            Text(
//                text = "Sign Up",
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            // Email Input Field
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text(text = "Email Address") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
//            )
//
//            // Password Input Field
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text(text = "Password") },
//                singleLine = true,
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(bottom = 16.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//            )
//
//            // Show Error Message
//            if (errorMessage.isNotEmpty()) {
//                Text(
//                    text = errorMessage,
//                    color = Color.Red,
//                    fontSize = 12.sp,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//            }
//
//            // Signup Button
//            Button(
//                onClick = {
//                    if (email.isNotEmpty() && password.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                        auth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
//                                    navController.navigate("login") // Navigate to login screen
//                                } else {
//                                    errorMessage = task.exception?.message ?: "Signup failed. Try again."
//                                }
//                            }
//                    } else {
//                        errorMessage = "Please enter a valid email and password."
//                    }
//                },
//                modifier = Modifier
//                    .width(300.dp)
//                    .padding(vertical = 8.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
//            ) {
//                Text(text = "Sign Up", color = Color.White)
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SignupScreenPreview() {
//    val mockNavController = rememberNavController()
//    val fakeAuth = FirebaseAuth.getInstance()
//    PinterestTheme {
//        SignupScreen(navController = mockNavController, auth = fakeAuth)
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email Address") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

            // Show Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Log In Button
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home") // Navigate to home screen
                                } else {
                                    errorMessage = task.exception?.message ?: "Login failed. Try again."
                                }
                            }
                    } else {
                        errorMessage = "Please enter a valid email and password."
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
            ) {
                Text(text = "Log In", color = Color.White)
            }

            // Forgot Password Button
            Text(
                text = "Forgot password?",
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { showResetDialog = true }
            )
        }
    }

    // Forgot Password Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email to reset your password.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                                    showResetDialog = false
                                } else {
                                    Toast.makeText(context, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PinterestTheme {
        val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
        val mockNavController = rememberNavController() // Mock NavController

        LoginScreen(navController = mockNavController, auth = mockAuth)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignupScreen(navController: NavController, auth: FirebaseAuth, database: DatabaseReference) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

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
            // Signup Heading
            Text(
                text = "Sign Up",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Username Input Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp)
            )

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email Address") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

            // Show Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Signup Button
            Button(
                onClick = {
                    if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    ) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Get the userId from Firebase Auth
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        // Create a User object
                                        val user = User(
                                            userId = userId,
                                            username = username,
                                            email = email,
                                            password = password
                                        )

                                        // Save user in Realtime Database
                                        database.child("users").child(userId).setValue(user)
                                            .addOnCompleteListener { dbTask ->
                                                if (dbTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Account created successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigate("login") // Navigate to login screen
                                                } else {
                                                    errorMessage = dbTask.exception?.message
                                                        ?: "Failed to save user data."
                                                }
                                            }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Signup failed. Try again."
                                }
                            }
                    } else {
                        errorMessage = "Please fill in all fields with valid information."
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Text(text = "Sign Up", color = Color.White)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountScreen(navController: NavController, auth: FirebaseAuth) {
    val context = LocalContext.current

    // State to hold the username
    var username by remember { mutableStateOf("Loading...") }

    // Fetch the current user
    val currentUser = auth.currentUser

    // Check if the user is logged in and fetch the username
    LaunchedEffect(currentUser) {
        currentUser?.let {
            val userId = it.uid
            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            database.child("username").get()
                .addOnSuccessListener { snapshot ->
                    username = snapshot.value as? String ?: "Unknown User"
                }
                .addOnFailureListener {
                    username = "Error fetching username"
                }
        } ?: run {
            username = "No user logged in"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(modifier = Modifier.weight(0.75f))

                Text(
                    text = "Your Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

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
                        text = username.firstOrNull()?.uppercase() ?: "U", // Use the first letter of the username
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // User details section
                Column {
                    Text(text = username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                        auth.signOut()
                        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("welcome") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
    val mockNavController = rememberNavController() // Mock NavController
    AccountScreen(navController = mockNavController, auth = mockAuth)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AvatarScreen(navController: NavController, auth: FirebaseAuth) {
    val currentUser = auth.currentUser
    var username by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }

    // Fetch username and email from the Realtime Database
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            email = user.email ?: "No Email"

            val userId = user.uid
            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            database.child("username").get()
                .addOnSuccessListener { snapshot ->
                    username = snapshot.value as? String ?: "Unknown User"
                }
                .addOnFailureListener {
                    username = "Error fetching username"
                }
        } ?: run {
            username = "No user logged in"
            email = "No Email"
        }
    }

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
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    // Handle share action
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        },
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
                        text = username.firstOrNull()?.uppercase() ?: "U",  // Use first letter of username
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
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Followers and following info
//                Text(
//                    text = "0 followers · 0 following",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button
            Button(
                onClick = {
                    navController.navigate("profile")
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
    val mockNavController = rememberNavController() // Mock NavController
    val mockAuth = FirebaseAuth.getInstance() // Mock FirebaseAuth
    AvatarScreen(navController = mockNavController, auth = mockAuth)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(navController: NavController, auth: FirebaseAuth) {
    val currentUser = auth.currentUser
    val database = FirebaseDatabase.getInstance().reference
    var username by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Fetch the current username from the database
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).child("username").get()
                .addOnSuccessListener { snapshot ->
                    username = snapshot.value as? String ?: ""
                }
                .addOnFailureListener {
                    errorMessage = "Failed to fetch username."
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        textAlign = TextAlign.Start, // Align text to the start
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 75.dp) // Adjust the padding for the left alignment
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Space between the TopAppBar and the content
            Spacer(modifier = Modifier.height(50.dp))

            // Display helper text
            Text(
                text = "Update your username below.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username Input Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Display
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Done Button at Bottom Center
            Spacer(modifier = Modifier.weight(1f)) // Push button to the bottom
            Button(
                onClick = {
                    currentUser?.let { user ->
                        val userId = user.uid
                        if (username.isNotEmpty()) {
                            database.child("users").child(userId).child("username").setValue(username)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Username updated successfully!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    errorMessage = "Failed to update username."
                                }
                        } else {
                            errorMessage = "Username cannot be empty."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Done", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val mockNavController = rememberNavController() // Mock NavController for preview purposes
    val fakeAuth = FirebaseAuth.getInstance()
    EditProfileScreen(navController = mockNavController, auth = fakeAuth)
}

