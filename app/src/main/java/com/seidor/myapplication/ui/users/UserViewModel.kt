package com.seidor.myapplication.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seidor.domain.model.User
import com.seidor.domain.usecase.GetUsersPageUseCase
import com.seidor.domain.usecase.GetUsersUseCase
import com.seidor.domain.usecase.RefreshUsersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UsersUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val query: String? = null,
    val users: List<User> = emptyList(),
    val endReached: Boolean = false,
    val errorFullScreen: String? = null,
    val syncError: String? = null
)

class UserViewModel(
    private val observeUsers: GetUsersUseCase,
    private val getUsersPage: GetUsersPageUseCase,
    private val refreshUsers: RefreshUsersUseCase
) : ViewModel() {

    private val PAGE_SIZE = 20
    private var currentOffset = 0
    private var loadingMoreJob: Job? = null

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observe(null)
        refresh()
    }

    /** Observa a lista local, reagindo a mudanças no Room. */
    fun observe(query: String?) {
        _uiState.update {
            it.copy(
                query = query,
                errorFullScreen = null,
                syncError = null,
                endReached = false,
                isLoadingMore = false
            )
        }

        // Reset de paginação
        currentOffset = 0

        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeUsers(query)
                .collect { localList ->
                    _uiState.update { it.copy(users = localList) }
                }
        }
    }

    /** Exemplo de busca paginada local. */
    suspend fun page(query: String?, limit: Int, offset: Int): List<User> {
        return getUsersPage(query, limit, offset)
    }

    /** Paginação local simulada. */
    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || _uiState.value.endReached) return

        loadingMoreJob?.cancel()
        loadingMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val query = _uiState.value.query
            val next = page(query, PAGE_SIZE, currentOffset)

            if (next.isEmpty()) {
                _uiState.update { it.copy(endReached = true, isLoadingMore = false) }
            } else {
                currentOffset += PAGE_SIZE

                _uiState.update {
                    it.copy(
                        users = it.users + next,
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    /** Força sincronização remota → salva local. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    syncError = null,
                    errorFullScreen = null,
                    endReached = false
                )
            }

            runCatching { refreshUsers() }.onFailure { e ->
                if (_uiState.value.users.isEmpty()) {
                    _uiState.update { it.copy(errorFullScreen = e.message) }
                } else {
                    _uiState.update { it.copy(syncError = "Falha ao sincronizar com o servidor") }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}