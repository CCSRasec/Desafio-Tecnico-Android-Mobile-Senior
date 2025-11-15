package com.seidor.myapplication.ui.users

import com.seidor.domain.model.Address
import com.seidor.domain.model.Company
import com.seidor.domain.model.Geo
import com.seidor.domain.model.User
import com.seidor.domain.usecase.GetUsersPageUseCase
import com.seidor.domain.usecase.GetUsersUseCase
import com.seidor.domain.usecase.RefreshUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Testes unitários do UserViewModel.
 *
 * Focamos aqui em:
 *  - Observação da lista de usuários local (observeUsers)
 *  - Paginação local (loadNextPage)
 *  - Tratamento de erro no refresh() (errorFullScreen vs syncError)
 */
class UserViewModelTest {

    // Dispatcher de teste para controlar o "Main"
    private val testDispatcher = StandardTestDispatcher()

    // Use cases mockados
    private lateinit var getUsersUseCase: GetUsersUseCase
    private lateinit var getUsersPageUseCase: GetUsersPageUseCase
    private lateinit var refreshUsersUseCase: RefreshUsersUseCase

    @Before
    fun setup() {
        // Seta o dispatcher de teste como Main
        Dispatchers.setMain(testDispatcher)

        // Cria mocks
        getUsersUseCase = mock()
        getUsersPageUseCase = mock()
        refreshUsersUseCase = mock()
    }

    @After
    fun tearDown() {
        // Restaura o dispatcher Main original
        Dispatchers.resetMain()
    }

    /**
     * Cenário 1:
     *  - observeUsers() devolve uma lista local com 2 usuários
     *  - Ao criar o ViewModel, o init chama observe(null) + refresh()
     *  - Queremos garantir que o uiState.users é preenchido com a lista observada
     */
    @Test
    fun `quando observar usuarios locais, uiState deve refletir a lista retornada`() = runTest(testDispatcher) {
        // Arrange
        val user1 = fakeUser(1)
        val user2 = fakeUser(2)

        // Simula fluxo de dados locais
        whenever(getUsersUseCase.invoke(null)).thenReturn(
            flowOf(listOf(user1, user2))
        )

        // refreshUsers não faz nada especial aqui
        whenever(refreshUsersUseCase.invoke()).thenReturn(Unit)

        // Act
        val viewModel = UserViewModel(
            observeUsers = getUsersUseCase,
            getUsersPage = getUsersPageUseCase,
            refreshUsers = refreshUsersUseCase
        )

        // Deixa todas as corrotinas agendadas rodarem
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(2, state.users.size)
        assertEquals("User 1", state.users[0].name)
        assertEquals("User 2", state.users[1].name)
    }

    /**
     * Cenário 2:
     *  - Não há lista local (observeUsers -> lista vazia)
     *  - loadNextPage() consulta o use case de paginação duas vezes:
     *      1ª chamada: devolve 2 usuários
     *      2ª chamada: devolve lista vazia -> endReached = true
     */
    @Test
    fun `loadNextPage deve adicionar usuarios e marcar endReached quando nao houver mais itens`() = runTest(testDispatcher) {
        // Arrange
        // Lista local vazia
        whenever(getUsersUseCase.invoke(null)).thenReturn(flowOf(emptyList()))

        // refreshUsers não faz nada relevante aqui
        whenever(refreshUsersUseCase.invoke()).thenReturn(Unit)

        val firstPageUsers = (1..2).map { fakeUser(it) }

        // Configura o use case de página:
        // - 1ª vez retorna a primeira página
        // - 2ª vez retorna vazio (sem mais itens)
        whenever(getUsersPageUseCase.invoke(anyOrNull(), any(), any()))
            .thenReturn(firstPageUsers) // primeira chamada
            .thenReturn(emptyList())    // segunda chamada

        val viewModel = UserViewModel(
            observeUsers = getUsersUseCase,
            getUsersPage = getUsersPageUseCase,
            refreshUsers = refreshUsersUseCase
        )

        advanceUntilIdle() // init

        // Act 1 - primeira página
        viewModel.loadNextPage()
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals(2, state.users.size)
        assertEquals("User 1", state.users[0].name)
        assertEquals("User 2", state.users[1].name)
        // Ainda não chegou ao fim
        assertTrue(!state.endReached)

        // Act 2 - segunda página (sem resultados)
        viewModel.loadNextPage()
        advanceUntilIdle()

        state = viewModel.uiState.value
        // Continua com os 2 usuários anteriores
        assertEquals(2, state.users.size)
        // Agora chegou ao fim
        assertTrue(state.endReached)
    }

    /**
     * Cenário 3:
     *  - Não há dados locais (users vazio)
     *  - refreshUsers lança exceção
     *  - Esperado:
     *      errorFullScreen = mensagem da exceção
     *      syncError = null
     */
    @Test
    fun `refresh com erro e sem dados locais deve preencher errorFullScreen`() = runTest(testDispatcher) {
        // Arrange
        // Nenhum dado local
        whenever(getUsersUseCase.invoke(null)).thenReturn(flowOf(emptyList()))

        // refresh lança erro
        val exception = RuntimeException("Erro de rede")
        whenever(refreshUsersUseCase.invoke()).thenThrow(exception)

        val viewModel = UserViewModel(
            observeUsers = getUsersUseCase,
            getUsersPage = getUsersPageUseCase,
            refreshUsers = refreshUsersUseCase
        )

        advanceUntilIdle() // roda init (observe + refresh)

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Erro de rede", state.errorFullScreen)
        // Como não tem dados locais, syncError não deve ser usado
        assertEquals(null, state.syncError)
    }

    /**
     * Cenário 4:
     *  - Já existem dados locais (users não vazio)
     *  - refreshUsers lança exceção
     *  - Esperado:
     *      errorFullScreen = null
     *      syncError = "Falha ao sincronizar com o servidor"
     */
    @Test
    fun `refresh com erro e com dados locais deve preencher syncError`() = runTest(testDispatcher) {
        // Arrange
        val localUser = fakeUser(1)

        // Já vem com uma lista local do Room
        whenever(getUsersUseCase.invoke(null)).thenReturn(flowOf(listOf(localUser)))

        // refresh lança erro
        whenever(refreshUsersUseCase.invoke()).thenThrow(RuntimeException("Erro qualquer"))

        val viewModel = UserViewModel(
            observeUsers = getUsersUseCase,
            getUsersPage = getUsersPageUseCase,
            refreshUsers = refreshUsersUseCase
        )

        advanceUntilIdle() // roda init

        // Assert
        val state = viewModel.uiState.value
        // Tem dados locais
        assertEquals(1, state.users.size)
        // Não deve mostrar erro full screen
        assertEquals(null, state.errorFullScreen)
        // Deve mostrar erro de sincronização "leve"
        assertEquals("Falha ao sincronizar com o servidor", state.syncError)
    }

    private fun fakeUser(
        id: Int,
        name: String = "User $id"
    ): User {
        return User(
            id = id,
            name = name,
            username = "user$id",
            email = "user$id@test.com",
            address = Address(
                street = "Street $id",
                suite = "Suite $id",
                city = "City $id",
                zipcode = "0000$id",
                geo = Geo(
                    lat = "0.00$id",
                    lng = "-0.00$id"
                )
            ),
            phone = "1111-000$id",
            website = "www.user$id.com",
            company = Company(
                name = "Company $id",
                catchPhrase = "CatchPhrase $id",
                bs = "BS $id"
            )
        )
    }

}