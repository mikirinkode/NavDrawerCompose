package com.mikirinkode.navdrawer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikirinkode.navdrawer.ui.theme.NavDrawerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavDrawerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyNavDrawerApp()
                }
            }
        }
    }
}

@Composable
fun MyNavDrawerApp() {
    /**
     * scaffoldState merupakan state bawaan dari scaffold untuk mengatur elemen di dalamnya dengan animasi default.
     * di dalamnya terdapat dua state, yakni drawer state untuk mengatur navigation drawer
     * dan snackbarHostState untuk mengatur Snackbar
     */
    val scaffoldState = rememberScaffoldState()

    /**
     * rememberCoroutineScope digunakan untuk memanggil Coroutine di dalam composable.
     * karena fungsi open merupakan suspend function, anda perlu menggunakan coroutine scope untuk memanggilnya
     *
     */
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MyTopBar(onMenuClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            })
        },
        drawerContent = {
            MyDrawerContent(
                onItemSelected = { title ->
                    scope.launch {
                        scaffoldState.drawerState.close()
                        val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = context.resources.getString(R.string.coming_soon, title),
                            actionLabel = context.resources.getString(R.string.subscribe_question)
                        )

                        // membaca aksi yang diberikan pada snackbar
                        if (snackbarResult == SnackbarResult.ActionPerformed){
                            Toast.makeText(context, context.resources.getString(R.string.subscribed_info), Toast.LENGTH_SHORT).show()
                        } else if (snackbarResult == SnackbarResult.Dismissed){
                            Toast.makeText(context, "dismissed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onBackPress = {
                    if (scaffoldState.drawerState.isOpen){
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
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

@Composable
fun MyDrawerContent(modifier: Modifier = Modifier, onItemSelected: (title: String) -> Unit, onBackPress: () -> Unit) {
    // item yang akan ditampilkan pada drawer
    val items =
        listOf(
            MenuItem(
                title = stringResource(id = R.string.home),
                icon = Icons.Default.Home
            ),
            MenuItem(
                title = stringResource(id = R.string.favourite),
                icon = Icons.Default.Favorite
            ),
            MenuItem(
                title = stringResource(id = R.string.profile),
                icon = Icons.Default.AccountCircle
            ),
        )
    Column(modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .height(190.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        )
        for (item in items) {
            Row(
                modifier = Modifier
                    .clickable { onItemSelected(item.title) }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(text = item.title, style = MaterialTheme.typography.subtitle2)
            }
        }
        Divider()
    }

    // setting on back pressed
    BackPressHandler{
        onBackPress()
    }
}

@Composable
fun BackPressHandler(enabled: Boolean = true, onBackPressed: () -> Unit) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }

        }
    }

    SideEffect {
        backCallback.isEnabled = enabled
    }

    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current){
        "No onBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher){
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose{
            backCallback.remove()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrawerContentPreview() {
    NavDrawerTheme {
//        MyDrawerContent(onItemSelected = {})
    }
}

@Composable
fun MyTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(id = R.string.menu)
                )
            }
        },
        title = {
            Text(stringResource(id = R.string.app_name))
        },
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun MyNavDrawerAppPreview() {
    NavDrawerTheme {
        MyNavDrawerApp()
    }
}

data class MenuItem(val title: String, val icon: ImageVector)