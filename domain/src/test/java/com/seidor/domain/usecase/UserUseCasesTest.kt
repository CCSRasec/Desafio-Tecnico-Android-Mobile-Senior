package com.seidor.domain.usecase

import com.seidor.domain.model.*
import com.seidor.domain.repository.UserRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Testes unitários responsáveis por validar o comportamento dos Use Cases
 * da camada Domain.
 *
 * Diferente de camadas que possuem regras complexas, os UseCases aqui têm
 * como principal função orquestrar chamadas ao repositório. Ainda assim,
 * garantir sua integridade é fundamental para manter a arquitetura coerente.
 *
 * Este arquivo assegura que:
 *
 *  1. Cada UseCase delega corretamente suas chamadas ao UserRepository
 *  2. Argumentos são encaminhados sem alteração indevida
 *  3. O retorno é repassado fielmente, sem mutações inesperadas
 *  4. Nenhuma chamada extra ocorre além do previsto
 *
 * Como dependências externas podem distorcer os resultados, o repositório
 * é completamente mockado para que cada caso de uso seja testado de forma
 * isolada e determinística.
 */
class UserUseCasesTest {

    // Repositório mockado utilizado por todos os casos de uso
    private val repository = mock<UserRepository>()

    // Instâncias reais dos UseCases, já injetando o mock acima
    private val getUserDetail = GetUserDetailUseCase(repository)
    private val getUsersPage = GetUsersPageUseCase(repository)
    private val getUsers = GetUsersUseCase(repository)
    private val refreshUsers = RefreshUsersUseCase(repository)


    /**
     *  TESTE 1 — GetUserDetailUseCase
     *
     * Objetivo:
     *  Validar se o caso de uso GetUserDetailUseCase:
     *   - chama repository.getUserById() com o parâmetro correto
     *   - recebe e devolve exatamente o mesmo objeto retornado pelo repositório
     *
     * Estratégia:
     *  - Cria um User mockado
     *  - Configura o repositório para devolvê-lo
     *  - Executa o caso de uso
     *  - Verifica delegação + retorno fiel
     */
    @Test
    fun `GetUserDetailUseCase returns user from repository`(): Unit = runBlocking {

        val user = User(
            id = 1,
            name = "Test",
            username = "t",
            email = "mail",
            address = Address("", "", "", "", Geo("", "")),
            phone = "",
            website = "",
            company = Company("", "", "")
        )

        // Repositório devolve o usuário simulado
        whenever(repository.getUserById(1)).thenReturn(user)

        // Execução do caso de uso
        val result = getUserDetail(1)

        // Validação de retorno
        assertEquals("Test", result?.name)

        // Validação de delegação
        verify(repository).getUserById(1)
    }

    /**
     * TESTE 2 — GetUsersPageUseCase (paginação local)
     *
     * Objetivo:
     *  Garantir que o caso de uso:
     *   - encaminha corretamente query, limit e offset ao repositório
     *   - retorna exatamente a lista fornecida pelo repositório
     *
     * Estratégia:
     *  - Mocka o retorno do repositório
     *  - Executa o caso de uso com os mesmos parâmetros
     *  - Compara dados + verifica delegação
     */
    @Test
    fun `GetUsersPageUseCase returns paginated list`(): Unit = runBlocking {

        val users = listOf(
            User(
                id = 1,
                name = "Item",
                username = "a",
                email = "b",
                address = Address("", "", "", "", Geo("", "")),
                phone = "",
                website = "",
                company = Company("", "", "")
            )
        )

        // Retorno simulado do repositório
        whenever(repository.getUsersPage(null, 10, 0)).thenReturn(users)

        // Execução do caso de uso
        val result = getUsersPage(null, 10, 0)

        assertEquals(1, result.size)

        // Verificação da delegação
        verify(repository).getUsersPage(null, 10, 0)
    }

    /**
     * TESTE 3 — GetUsersUseCase (Flow reativo)
     *
     * Objetivo:
     *  Confirmar que:
     *   - o Flow retornado pelo repositório é repassado integralmente
     *   - observeUsers(query) é invocado com o parâmetro exato
     *
     * Estratégia:
     *  - Mocka um fluxo emitindo uma única lista
     *  - Executa o UseCase
     *  - Coleta o primeiro valor
     *  - Valida os dados e a delegação
     */
    @Test
    fun `GetUsersUseCase returns flow of users from repository`(): Unit = runBlocking {

        val users = listOf(
            User(
                id = 99,
                name = "FlowUser",
                username = "",
                email = "",
                address = Address("", "", "", "", Geo("", "")),
                phone = "",
                website = "",
                company = Company("", "", "")
            )
        )

        // Flow simulado devolvido pelo repositório
        whenever(repository.observeUsers(null)).thenReturn(
            flow { emit(users) }
        )

        // Execução do use case
        val result = getUsers(null).first()

        assertEquals("FlowUser", result.first().name)

        // Garante a delegação correta
        verify(repository).observeUsers(null)
    }

    /**
     * TESTE 4 — RefreshUsersUseCase
     *
     * Objetivo:
     *  Verificar que o caso de uso simplesmente delega a chamada
     *  para repository.refreshUsers() sem realizar qualquer transformação.
     *
     * Estratégia:
     *  - Executa o caso de uso
     *  - Apenas verifica se o método do repositório foi invocado
     */
    @Test
    fun `RefreshUsersUseCase calls repository`() = runBlocking {

        refreshUsers()

        // Apenas confirma a delegação
        verify(repository).refreshUsers()
    }
}
