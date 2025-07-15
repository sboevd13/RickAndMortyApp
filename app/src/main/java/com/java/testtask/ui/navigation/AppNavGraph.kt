package com.java.testtask.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.java.testtask.ui.characters.CharactersScreen
import com.java.testtask.ui.details.CharacterDetailScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "characters_list") {
        composable("characters_list") {
            val viewModel = hiltViewModel<com.java.testtask.ui.characters.CharactersViewModel>()
            CharactersScreen(
                viewModel = viewModel,
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }
        composable(
            route = "character_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val viewModel = hiltViewModel<com.java.testtask.ui.details.CharacterDetailViewModel>()
            CharacterDetailScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}