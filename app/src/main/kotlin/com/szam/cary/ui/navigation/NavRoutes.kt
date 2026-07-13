package com.szam.cary.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object LostItems : Screen("lost_items")
    object FoundItems : Screen("found_items")
    object AddLostItem : Screen("add_lost_item")
    object EditLostItem : Screen("edit_lost_item/{itemId}") {
        fun createRoute(itemId: String) = "edit_lost_item/$itemId"
    }
    object AddFoundItem : Screen("add_found_item")
    object EditFoundItem : Screen("edit_found_item/{itemId}") {
        fun createRoute(itemId: String) = "edit_found_item/$itemId"
    }
    object DetailLostItem : Screen("detail_lost_item/{itemId}") {
        fun createRoute(itemId: String) = "detail_lost_item/$itemId"
    }
    object DetailFoundItem : Screen("detail_found_item/{itemId}") {
        fun createRoute(itemId: String) = "detail_found_item/$itemId"
    }
    object Claim : Screen("claim/{itemId}/{itemType}") {
        fun createRoute(itemId: String, itemType: String) = "claim/$itemId/$itemType"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object Search : Screen("search")
}
