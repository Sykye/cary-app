package com.szam.cary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.szam.cary.ui.screen.*
import com.szam.cary.viewmodel.*

@Composable
fun CaryNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val lostItemViewModel: LostItemViewModel = viewModel(factory = LostItemViewModel.Factory)
    val foundItemViewModel: FoundItemViewModel = viewModel(factory = FoundItemViewModel.Factory)
    val claimViewModel: ClaimViewModel = viewModel(factory = ClaimViewModel.Factory)
    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                lostItemViewModel = lostItemViewModel,
                foundItemViewModel = foundItemViewModel,
                onNavigateToLostItems = { navController.navigate(Screen.LostItems.route) },
                onNavigateToFoundItems = { navController.navigate(Screen.FoundItems.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToDetailLost = { id -> navController.navigate(Screen.DetailLostItem.createRoute(id)) },
                onNavigateToDetailFound = { id -> navController.navigate(Screen.DetailFoundItem.createRoute(id)) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LostItems.route) {
            LostItemScreen(
                lostItemViewModel = lostItemViewModel,
                authViewModel = authViewModel,
                onNavigateToAdd = { navController.navigate(Screen.AddLostItem.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.DetailLostItem.createRoute(id)) },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditLostItem.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FoundItems.route) {
            FoundItemScreen(
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onNavigateToAdd = { navController.navigate(Screen.AddFoundItem.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.DetailFoundItem.createRoute(id)) },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditFoundItem.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddLostItem.route) {
            AddLostItemScreen(
                lostItemViewModel = lostItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditLostItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            EditLostItemScreen(
                itemId = itemId,
                lostItemViewModel = lostItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.AddFoundItem.route) {
            AddFoundItemScreen(
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditFoundItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            EditFoundItemScreen(
                itemId = itemId,
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DetailLostItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailLostItemScreen(
                itemId = itemId,
                lostItemViewModel = lostItemViewModel,
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditLostItem.createRoute(id)) },
                onNavigateToClaim = { id, type -> navController.navigate(Screen.Claim.createRoute(id, type)) },
                onNavigateToDetailFound = { id -> navController.navigate(Screen.DetailFoundItem.createRoute(id)) }
            )
        }

        composable(
            route = Screen.DetailFoundItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailFoundItemScreen(
                itemId = itemId,
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditFoundItem.createRoute(id)) },
                onNavigateToClaim = { id, type -> navController.navigate(Screen.Claim.createRoute(id, type)) }
            )
        }

        composable(
            route = Screen.Claim.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("itemType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val itemType = backStackEntry.arguments?.getString("itemType") ?: "found"
            ClaimScreen(
                itemId = itemId,
                itemType = itemType,
                claimViewModel = claimViewModel,
                foundItemViewModel = foundItemViewModel,
                lostItemViewModel = lostItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                claimViewModel = claimViewModel,
                lostItemViewModel = lostItemViewModel,
                foundItemViewModel = foundItemViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetailLost = { id -> navController.navigate(Screen.DetailLostItem.createRoute(id)) },
                onNavigateToDetailFound = { id -> navController.navigate(Screen.DetailFoundItem.createRoute(id)) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetailLost = { id -> navController.navigate(Screen.DetailLostItem.createRoute(id)) },
                onNavigateToDetailFound = { id -> navController.navigate(Screen.DetailFoundItem.createRoute(id)) }
            )
        }
    }
}
