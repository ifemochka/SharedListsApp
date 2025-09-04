package com.example.sharedlistsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sharedlistsapp.data.Screen
import com.example.sharedlistsapp.ui.login.LoginScreen
import com.example.sharedlistsapp.ui.main.MainScreen
import com.example.sharedlistsapp.ui.sharedlist.SharedListScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onAuthSuccess = { uid, email ->
                            val route = Screen.Main.createRoute(uid, email)
                            navController.navigate(route)
                        }
                    )
                }

                composable(
                    route = Screen.Main.route,
                    arguments = listOf(
                        navArgument("uid") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val uid = backStackEntry.arguments?.getString("uid") ?: ""
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    MainScreen(
                        uid = uid,
                        email = email,
                        onLogout = {
                            navController.popBackStack(Screen.Login.route, false)
                        },
                        onOpenList = { listName ->
                            val route = Screen.SharedList.createRoute(uid, listName)
                            navController.navigate(route)
                        }
                    )
                }

                composable(
                    route = Screen.SharedList.route,
                    arguments = listOf(
                        navArgument("uid") { type = NavType.StringType },
                        navArgument("listName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val uid = backStackEntry.arguments?.getString("uid") ?: ""
                    val listName = backStackEntry.arguments?.getString("listName") ?: ""

                    SharedListScreen(
                        uid = uid,
                        listName = listName
                    )
                }
            }
        }
    }
}