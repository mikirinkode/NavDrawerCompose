package com.mikirinkode.navdrawer

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@Composable
fun MyNavDrawerApp() {
    /**
     * scaffoldState merupakan state bawaan dari scaffold untuk mengatur elemen di dalamnya dengan animasi default.
     * di dalamnya terdapat dua state, yakni drawer state untuk mengatur navigation drawer
     * dan snackbarHostState untuk mengatur Snackbar
     */
//    val scaffoldState = rememberScaffoldState()

    /**
     * rememberCoroutineScope digunakan untuk memanggil Coroutine di dalam composable.
     * karena fungsi open merupakan suspend function, anda perlu menggunakan coroutine scope untuk memanggilnya
     *
     */
//    val scope = rememberCoroutineScope()

//    val context = LocalContext.current
    val appState = rememberMyNavDrawerState()

    Scaffold(
        scaffoldState = appState.scaffoldState,
        topBar = {
            MyTopBar(onMenuClick = appState::onMenuClick)
        },
        drawerContent = {
            MyDrawerContent(
                onItemSelected = appState::onItemSelected,
                onBackPress = appState::onBackPressed
            )
        },
        drawerGesturesEnabled = appState.scaffoldState.drawerState.isOpen
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.hello_world))
        }
    }
}