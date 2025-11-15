package com.seidor.myapplication.ui.users.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seidor.myapplication.ui.users.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun UserComposeList(
    viewModel: UserViewModel,
    onClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.refresh() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        // ===========================
        // 1. FULL-SCREEN ERROR STATE
        // ===========================
        if (state.errorFullScreen != null) {
            FullErrorState(
                message = state.errorFullScreen!!,
                onRetry = { viewModel.refresh() }
            )
        } else {
            // ===========================
            // 2. LISTA NORMAL
            // ===========================
            Column {

                var search by remember { mutableStateOf("") }

                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = search,
                            onQueryChange = {
                                search = it
                                viewModel.observe(it.ifBlank { null })
                            },
                            onSearch = { viewModel.observe(search.ifBlank { null }) },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("Buscar usuário / e-mail") }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {}

                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {

                    items(state.users) { user ->
                        UserItemComposable(
                            user = user,
                            onClick = { onClick(user.id) }
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastIndex ->
                            val total = listState.layoutInfo.totalItemsCount
                            if (lastIndex != null && lastIndex >= total - 3) {
                                viewModel.loadNextPage()
                            }
                        }
                }

            }

            // =================================
            // 3. WARNING BANNER PARA SYNC ERROR
            // =================================
            if (state.syncError != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    SyncErrorBanner(state.syncError!!)
                }
            }
        }

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
