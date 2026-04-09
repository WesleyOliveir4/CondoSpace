package com.example.condospace.presentation.ui.feature.condominium.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.condominium.components.CurrentCondominiumCard
import com.example.condospace.presentation.ui.feature.condominium.components.SearchCondominiumCard
import com.example.condospace.presentation.ui.feature.condominium.state.CondominiumState
import com.example.condospace.presentation.ui.feature.condominium.viewmodel.SelectCondominiumViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelectCondominiumScreen(
    navController: NavHostController,
    userId: String,
    navigateToLogin: () -> Unit,
    registerFlow: Boolean,
) {
    val condominiumViewModel: SelectCondominiumViewModel = koinViewModel()
    val condominiumState by condominiumViewModel.condominiumState.collectAsStateWithLifecycle()
    val searchResults by condominiumViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by condominiumViewModel.isSearching.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val successMsg = stringResource(R.string.condominium_update_success)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        condominiumViewModel.fetchUserCondominium(userId)
    }

    LaunchedEffect(condominiumState) {
        val state = condominiumState
        when (state) {
            is CondominiumState.Success if state.saveSuccess -> {
                if (registerFlow) {
                    navigateToLogin()
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = successMsg,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                condominiumViewModel.resetActionState()
            }

            is CondominiumState.Success if state.error != null -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.error,
                        duration = SnackbarDuration.Short
                    )
                }
                condominiumViewModel.resetActionState()
            }

            is CondominiumState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            else -> {}
        }
    }

    CondoSpaceTheme {
        SelectCondominiumScreenContent(
            navController = navController,
            snackbarHostState = snackbarHostState,
            condominiumState = condominiumState,
            searchResults = searchResults,
            isSearching = isSearching,
            onSearchClick = { condominiumViewModel.searchCondominiumByCep(it) },
            onSaveCondominiumSelectedClick = { condominiumViewModel.saveCondominiumSelected(userId, it) },
            onSaveCondominiumCreateClick = { condominiumViewModel.saveCondominiumCreated(userId, it) },
            isSaving = (condominiumState as? CondominiumState.Success)?.isSaving == true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCondominiumScreenContent(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    condominiumState: CondominiumState,
    searchResults: List<CondominiumUiModel>,
    isSearching: Boolean,
    onSearchClick: (String) -> Unit,
    onSaveCondominiumSelectedClick: (CondominiumUiModel) -> Unit,
    onSaveCondominiumCreateClick: (CondominiumUiModel) -> Unit,
    isSaving: Boolean,
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = stringResource(id = R.string.condominium_title),
                onBackClick = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val currentCondo = if (condominiumState is CondominiumState.Success) {
                    condominiumState.selectedCondominium
                } else null

                SelectCondominiumLayout(
                    currentCondo = currentCondo,
                    searchResults = searchResults,
                    isSearching = isSearching,
                    isSaving = isSaving,
                    onSearchClick = onSearchClick,
                    onSaveCondominiumSelectedClick = onSaveCondominiumSelectedClick,
                    onSaveCondominiumCreateClick = onSaveCondominiumCreateClick
                )

                AnimatedVisibility(
                    visible = condominiumState is CondominiumState.Loading || isSaving,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val isInitialLoading = condominiumState is CondominiumState.Loading && currentCondo == null
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isInitialLoading) Modifier.background(MaterialTheme.colorScheme.background)
                                else Modifier
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .pointerInput(Unit) {}
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF354EAB))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectCondominiumLayout(
    currentCondo: CondominiumUiModel?,
    searchResults: List<CondominiumUiModel>,
    isSearching: Boolean,
    isSaving: Boolean,
    onSearchClick: (String) -> Unit,
    onSaveCondominiumSelectedClick: (CondominiumUiModel) -> Unit,
    onSaveCondominiumCreateClick: (CondominiumUiModel) -> Unit
) {
    var isEditing by remember { mutableStateOf(currentCondo == null) }
    
    LaunchedEffect(currentCondo) {
        if (currentCondo != null) {
            isEditing = false
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.condominium_config_address),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.condominium_config_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        CurrentCondominiumCard(
            selectedCondo = currentCondo,
            onEditClick = { isEditing = true }
        )

        AnimatedContent(
            targetState = isEditing,
            transitionSpec = {
                if (targetState) {
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                } else {
                    (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
                }
            },
            label = "SearchTransition"
        ) { editing ->
            if (editing) {
                SearchCondominiumCard(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    isSaving = isSaving,
                    onSearchClick = onSearchClick,
                    onSaveCondominiumSelectedClick = onSaveCondominiumSelectedClick,
                    onSaveCondominiumCreateClick = onSaveCondominiumCreateClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectCondominiumScreenPreview() {
    val navController = rememberNavController()
    CondoSpaceTheme {
        SelectCondominiumScreenContent(
            navController = navController,
            snackbarHostState = remember { SnackbarHostState() },
            condominiumState = CondominiumState.Success(null),
            searchResults = emptyList(),
            isSearching = false,
            onSearchClick = {},
            onSaveCondominiumSelectedClick = {},
            onSaveCondominiumCreateClick = {},
            isSaving = false
        )
    }
}
