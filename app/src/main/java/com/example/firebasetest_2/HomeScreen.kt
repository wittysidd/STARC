package com.example.firebasetest_2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


const val govt = "Govt Login"
const val user = "User Login"
const val MNC = "MNC Login"
const val Cont = "Contractor Login"
var loginInfo : String = ""

@Composable
fun HomeScreen(navController : NavController) {
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.homescreen_image)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier
                    .padding(10.dp, 80.dp, 10.dp, 75.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome to",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "STARC",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    "Simplified Tender Automator for Road Construction",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle(1)
                )


            }

            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LoginButtons(
                    buttonTitle = govt,
                    iconOfButton = Icons.Default.AccountBox,
                    navController
                )
                LoginButtons(
                    buttonTitle = MNC,
                    iconOfButton = Icons.Default.AccountCircle,
                    navController
                )
                LoginButtons(buttonTitle = user, iconOfButton = Icons.Default.Person, navController)
                LoginButtons(buttonTitle = Cont, iconOfButton = Icons.Default.Person, navController)
            }
        }

    }
}



@Composable
fun LoginButtons(buttonTitle : String, iconOfButton : ImageVector, navController : NavController) {

    Button(
        onClick = {
            loginInfo = buttonTitle
            if (loginInfo == user)
            {
                navController.navigate("user_login_page")
            }
            else{
                navController.navigate(Screen.Login.route)
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(80.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(105, 204, 0, 220),
            contentColor = Color.Black
        )
    ) {
        Text(
            buttonTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Icon(imageVector = iconOfButton, contentDescription ="" , modifier = Modifier.size(35.dp))
    }
}

//
//@Composable
//fun ThreeDButton(onClick: () -> Unit) {
//    val gradient = Brush.verticalGradient(
//        colors = listOf(
//            Color(0xFFE0E0E0), // Light color at the top
//            Color(0xFFBDBDBD)  // Darker color at the bottom
//        )
//    )
//
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        modifier = Modifier
//            .padding(4.dp)
//            .shadow(8.dp, RoundedCornerShape(8.dp))
//    ) {
//        Box(
//            modifier = Modifier
//                .background(gradient)
//                .clickable(onClick = onClick)
//                .padding(horizontal = 24.dp, vertical = 12.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "3D Button",
//                color = Color.Black,
//                fontSize = 18.sp
//            )
//        }
//    }
//}