package com.seidor.myapplication.ui.users.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seidor.domain.model.User
import com.seidor.domain.usecase.GetUserDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val getUserDetailUseCase: GetUserDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<User?>(null)
    val state: StateFlow<User?> = _state

    fun load(userId: Int) {
        viewModelScope.launch {
            _state.value = getUserDetailUseCase(userId)
        }
    }
}
