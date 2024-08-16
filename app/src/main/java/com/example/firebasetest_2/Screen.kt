package com.example.firebasetest_2

sealed class Screen(val route:String) {
    object Home : Screen(route = "home_screen")
    object Login : Screen(route = "login_screen")
    object User : Screen(route = "user_screen")
    object MNC : Screen(route = "MNC_screen")
    object Contractor : Screen(route = "contractor_screen")
    object Govt : Screen(route = "govt_screen")
}