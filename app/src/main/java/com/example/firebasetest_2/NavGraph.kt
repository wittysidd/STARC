package com.example.firebasetest_2

import android.util.Log
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson


@Composable
fun SetupNavGraph(navController: NavHostController)
{
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(
            route = Screen.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1500 })  }
        ){
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.Login.route,
            enterTransition = { scaleIn(initialScale = 0f) },
            exitTransition = { scaleOut(targetScale = 0f) }

        ){
            LoginScreen(navController = navController)
        }

        composable(
            route = Screen.User.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1500 })  }
        ){
            UserScreen(navController = navController)
        }

        composable(
            route = Screen.MNC.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            MNCScreen(navController = navController)
        }

        composable(
            route = Screen.Contractor.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            val viewModel = myViewModel()
            var ownerName by remember { mutableStateOf("Contractor") }
            viewModel.getOwnerName { ownerName = it }
            ContractorScreen(navController = navController, ownerName)
        }

        composable(
            route = Screen.Govt.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            GovtScreen(navController = navController)
        }

        composable(
            route = "register_complaint_button",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            RegisterComplainButton(navController = navController)
        }

        composable(
            route = "check_for_complaints_screen",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            CheckForComplaintsScreen()
        }
        composable(
            route = "check_for_assignment_screen",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            CheckForAssignmentsScreen()
        }

        composable(
            route = "check_for_contracts_screen",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1500 })  },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1500 })  }
        ){
            CheckForContractsScreen()
        }

        composable(
            route = "history_of_complaints"
        ){
            HistoryScreen()
        }

        composable(
            route = "user_sign_up_page"
        ){
            UserSignUpPage(navController)
        }

        composable(
            route = "user_login_page"
        ){
            UserLoginPage(navController)
        }

        composable(
            route = "view_offers_screen"
        ){
            ViewOffersScreen(navController)
        }

        composable(
            route = "add_contractor_screen"
        ){
            AddContractorScreen(navController = navController)
        }

        composable(
            route = "view_submitted_contracts"
        ){
            ViewSubmittedContractsScreen()
        }

        composable(
            route = "view_updates_for_Complaint"
        ){
            ViewUpdatesForComplaintScreen(navController)
        }
        composable(
            "show_list_of_contractor_bids/{offersJson}",
            arguments = listOf(navArgument("offersJson") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val offersJson = backStackEntry.arguments?.getString("offersJson")
            Log.d("AppNavHost", "Received JSON: $offersJson")
            val offersOnContract = Gson().fromJson(offersJson, OffersOnContract::class.java)
            Log.d("AppNavHost", "Deserialized data: $offersOnContract")
            ShowListOfContractorBids(offersOnContract, navController)
        }
        composable(
            route = "update_current_works_screen"
        ){
            UpdateCurrentWorksScreen(navController)
        }
        composable(
            route = "check_for_updates_govt"
        ){
            CheckForActiveUpdates(navController)
        }
    }

}
