package com.example.firebasetest_2

import android.content.Context
import android.graphics.Paint
import android.graphics.Paint.Align
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController : NavController)      // common for all MNC<GOVT<CONTRACTORS
{
    var pageOwner = remember { mutableStateOf("") }
    val viewModel = myViewModel()
    pageOwner.value = loginInfo

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF0EDED), // Light color at the top
            Color(224, 158, 0, 255)  // Darker color at the bottom
        )
    )

        Box(modifier = Modifier
            .fillMaxWidth(1f)
            .height(70.dp)
            .background(gradient)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left){
                IconButton(onClick = { navController.popBackStack() }, Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
                    Icon(Icons.Default.ArrowBack,"", modifier = Modifier.size(35.dp))
                }
                Text(text = "${pageOwner.value} Screen", modifier = Modifier.padding(10.dp,5.dp,0.dp,0.dp),color=Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            modifier = Modifier
                .padding(0.dp, 100.dp, 0.dp, 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome back",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Text(
                "Please Enter your Login Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Email ID", color = Color.Black) }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Password", color = Color.Black) },
                    visualTransformation = PasswordVisualTransformation()
                )

                val context = LocalContext.current
                var counterForContractor by remember { mutableStateOf(0) }
                var showTextForNoAccount by remember { mutableStateOf(false) }
                var enabled by remember { mutableStateOf(true) }
                var progress by remember { mutableStateOf(false) }

                var backgroundImage: ImageBitmap =
                    ImageBitmap.imageResource(id = R.drawable.govt_screen_image)
                val imageForGovt =
                    ImageBitmap.imageResource(id = R.drawable.govt_screen_image)
                val imageForCont =
                    ImageBitmap.imageResource(id = R.drawable.contractor_screen_img)
                val imageForMNC = ImageBitmap.imageResource(id = R.drawable.mnc_screen_logo)
                if (loginInfo == govt) {
                    backgroundImage = imageForGovt
                } else if (loginInfo == MNC) {
                    backgroundImage = imageForMNC
                } else {
                    backgroundImage = imageForCont
                }

                Button(
                    onClick = {
                        if (!email.isNullOrEmpty() || !password.isNullOrBlank()) {

                            if ((loginInfo == MNC)) {
                                navController.navigate(Screen.MNC.route)
                            } else if ((loginInfo == govt)) {
                                navController.navigate(Screen.Govt.route)
                            } else if ((loginInfo == Cont)) {
                                enabled = false
                                progress = true
                                viewModel.ContractorLogin(email, password, navController,
                                    OnWrongEmail = {
                                        counterForContractor++
                                        Toast.makeText(
                                            context,
                                            "Invalid Credentials! Try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        enabled = true
                                        progress = false
                                    })
                                {
                                    // on No Account for such email
                                    showTextForNoAccount = true
                                    enabled = false
                                    progress = false
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Incorrect Email/Password !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please Enter Email & Password !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    },
                    enabled = enabled
                ) {
                    Text("Login")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "login"
                    )
                }

                if (progress == true) {
                    CircularProgressIndicator()
                    HideKeyboard()
                }
                if (counterForContractor == 3) {
                    Text(
                        "\tIt seems you forgot your Credentials,\n\t contact Government Service",
                        color = Color(255, 140, 60, 255)
                    )
                }
                if (showTextForNoAccount == true) {
                    Text("\tYour email ID is Incorrect", color = Color(229, 57, 53, 255))
                    Text(
                        "\nIf you don't have an Official Contractor Account, \nPlease contact Government Services and create an account after Verification",
                        color = Color(
                            240,
                            140,
                            60,
                            255
                        )
                    )
                }
                Image(
                    bitmap = backgroundImage,
                    contentDescription = null,
                    modifier = Modifier,
                )


            }


        }

    }
}

@Composable
fun UserLoginPage(navController : NavController)
{
    var pageOwner = remember { mutableStateOf("") }
    pageOwner.value = loginInfo
    val context = LocalContext.current
    var progress by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var showTextForEmail by remember{ mutableStateOf(false) }
    var emailSentAgain by remember{ mutableStateOf(false) }
    val loginFunc = LoginScreens()


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "This Page is for", modifier = Modifier.padding(0.dp,20.dp,0.dp,0.dp), color = Color.Red)
        Text(text = "\"${pageOwner.value}\"", modifier = Modifier, color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold,textAlign = TextAlign.Center)
        Text(text = "if mistaken then go back",modifier = Modifier.padding(0.dp,0.dp,0.dp,20.dp), color = Color.Red,textAlign = TextAlign.Center)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            modifier = Modifier
                .padding(0.dp, 200.dp, 0.dp, 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome back",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                "Please Enter your Login Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it}, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Email ID", color = Color.Black)}
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Password", color = Color.Black)},
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    if(email.isEmpty() || password.isEmpty())
                    {
                        Toast.makeText(context, "Enter your email and Password", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        progress = true
                        enabled = false
                        loginFunc.signIn(email,password, navController, verifyEmail = {
                            progress = false
                            enabled = true
                            showTextForEmail = true
                            emailSentAgain = true
                            Toast.makeText(context, "Please verify your email!", Toast.LENGTH_SHORT).show()
                        }){
                            progress = false
                            enabled = true
                            Toast.makeText(context,"Invalid email and password! Try again!", Toast.LENGTH_SHORT).show()
                        } }
                    },
                    enabled = enabled
                   ) {
                    Text("Login")
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "login")
                }


                if(progress)
                {
                    HideKeyboard()
                    CircularProgressIndicator()
                }
                if(emailSentAgain == true)
                {
                    Text("Verification email sent again !", color = Color(
                        2,
                        96,
                        0,
                        240
                    ))

                }
                if(showTextForEmail == true)
                {
                    Text("Your account was not Verified,Please check your emails to verify",
                        textAlign = TextAlign.Center,
                        color = Color(
                        206,
                        96,
                        0,
                        255
                    ))

                }
                Text(text = "New User? Create Account",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(onClick = { navController.navigate("user_sign_up_page") }),
                    color = Color.Blue
                    )


            }


        }

    }
}

@Composable
fun UserSignUpPage(navController : NavController) {

    val loginFunc = LoginScreens()
    var showText by remember { mutableStateOf(false) }
    var showTextForVerification by remember { mutableStateOf(false) }
    var emailSentText by remember { mutableStateOf(false) }
    var verifyAgainText by remember{ mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            modifier = Modifier
                .padding(0.dp, 40.dp, 0.dp, 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create new Account",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                "Please Enter your Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var name by remember { mutableStateOf("") }
            var number by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Name", color = Color.Black) },
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Number", color = Color.Black) }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Email ID", color = Color.Black) }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Create Password", color = Color.Black) },
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please Enter all fields", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        enabled = false
                        progress = true
                        showTextForVerification = true
                        loginFunc.signUp(email,
                            password,
                            OnSuccess = { Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show() },
                            emailSent = {emailSentText = true},
                            OnSameEmail = { showText = true
                            showTextForVerification = false
                            })
                    }
                },
                    enabled = enabled) {
                    Text("Sign Up")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "sign up"
                    )
                }


                if(showText)
                {
                    progress = false
                    Text("This email ID already Exists, Please go back and Login !", color = Color(229, 57, 53, 255))
                }
                if(showTextForVerification)
                {
                    Text("Please check your Email, \nClick on the link to verify", color = Color(255, 179, 0, 255), fontSize = 14.sp)
                    if(emailSentText){
                        progress = false
                        Text("\nEmail Sent Successfully!", color = Color(67, 160, 71, 255), fontStyle = FontStyle.Italic)
                    }
                    Text("Please click here after verification:", color = Color(255, 179, 0, 255),modifier=Modifier.padding(7.dp) ,fontSize = 16.sp)
                    Text(
                        "LOGIN SCREEN",
                        modifier = Modifier.clickable {
                            navController.popBackStack() },
                        color = Color(160, 24, 196, 255),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                        )
                }

                if(progress)
                {
                    HideKeyboard()
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HideKeyboard() {
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.hide()
}
