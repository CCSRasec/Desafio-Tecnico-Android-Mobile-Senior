package com.seidor.data.repository

import com.seidor.data.api.*
import com.seidor.data.local.dao.user.UserDao
import com.seidor.data.model.AddressApi
import com.seidor.data.model.CompanyApi
import com.seidor.data.model.GeoApi
import com.seidor.data.model.UserApi
import com.seidor.data.model.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Testes unitários responsáveis por validar o funcionamento do
 * UserRepositoryImpl, garantindo que:
 *
 *  - Chamadas à API ocorram corretamente
 *  - Operações no banco local (DAO) sejam executadas na ordem correta
 *  - Modelos sejam convertidos adequadamente entre camadas
 *
 * Como erros de repositório geram inconsistências silenciosas em toda
 * a aplicação, os testes isolam comportamento mockando tanto ApiService
 * quanto UserDao.
 */
class UserRepositoryImplTest {

    private val api = mock<ApiService>()
    private val dao = mock<UserDao>()
    private val repository = UserRepositoryImpl(api, dao)


    /**
     * TESTE 1 — refreshUsers()
     *
     * Objetivo:
     *  Validar que refreshUsers():
     *   - obtém usuários da API remota
     *   - limpa completamente o cache local
     *   - persiste os novos dados recebidos
     *
     * Estratégia:
     *  - Mockar o retorno da API
     *  - Executar refreshUsers()
     *  - Conferir se dao.clear() e dao.upsertAll() foram chamados
     */
    @Test
    fun `refreshUsers calls clear and inserts new users`() = runBlocking {

        val dto = listOf(
            UserApi(
                id = 1,
                name = "Teste",
                username = "user",
                email = "mail",
                phone = "",
                website = "",
                address = AddressApi(
                    street = "",
                    suite = "",
                    city = "",
                    zipcode = "",
                    geo = GeoApi("", "")
                ),
                company = CompanyApi(
                    name = "",
                    catchPhrase = "",
                    bs = ""
                )
            )
        )

        whenever(api.getUsers()).thenReturn(dto)

        repository.refreshUsers()

        verify(dao).clear()
        verify(dao).upsertAll(any())
    }


    /**
     * TESTE 2 — observeUsers()
     *
     * Objetivo:
     *  Garantir que o repositório:
     *   - leia corretamente o fluxo de UserEntity vindo do DAO
     *   - converta UserEntity → User (modelo de domínio)
     *
     * Estratégia:
     *  - Mockar observeAll() emitindo uma entidade
     *  - Coletar o primeiro valor de observeUsers()
     *  - Validar mapeamento do nome
     */
    @Test
    fun `observeUsers returns mapped local users`() = runBlocking {

        val entity = UserEntity(
            id = 1,
            name = "Local",
            username = "local",
            email = "local@mail",
            phone = "",
            website = "",
            street = "",
            suite = "",
            city = "",
            zipcode = "",
            lat = "",
            lng = "",
            companyName = "",
            companyCatchPhrase = "",
            companyBs = "",
            cacheTimestamp = 0L
        )

        whenever(dao.observeAll(null)).thenReturn(
            flow { emit(listOf(entity)) }
        )

        val result = repository.observeUsers(null).first()

        assertEquals("Local", result.first().name)
    }


    /**
     * TESTE 3 — getUsersPage()
     *
     * Objetivo:
     *  Validar que o repositório:
     *   - realize paginação via DAO (query, limit, offset)
     *   - mapeie UserEntity → User corretamente
     *
     * Estratégia:
     *  - Mockar dao.page()
     *  - Executar getUsersPage()
     *  - Checar o dado convertido
     */
    @Test
    fun `getUsersPage maps correctly`() = runBlocking {

        val entity = UserEntity(
            id = 1,
            name = "Local",
            username = "local",
            email = "local@mail",
            phone = "",
            website = "",
            street = "",
            suite = "",
            city = "",
            zipcode = "",
            lat = "",
            lng = "",
            companyName = "",
            companyCatchPhrase = "",
            companyBs = "",
            cacheTimestamp = 0L
        )

        whenever(dao.page(null, 10, 0)).thenReturn(listOf(entity))

        val page = repository.getUsersPage(null, 10, 0)

        assertEquals("Local", page.first().name)
    }


    /**
     * TESTE 4 — getUserById()
     *
     * Objetivo:
     *  Assegurar que:
     *   - o DAO seja consultado corretamente
     *   - o UserEntity seja convertido em User
     *
     * Estratégia:
     *  - Mock do DAO retornando UserEntity
     *  - Executar getUserById()
     *  - Validar campo essencial mapeado
     */
    @Test
    fun `getUserById returns mapped user`() = runBlocking {

        val entity = UserEntity(
            id = 1,
            name = "Local",
            username = "local",
            email = "mail",
            phone = "",
            website = "",
            street = "",
            suite = "",
            city = "",
            zipcode = "",
            lat = "",
            lng = "",
            companyName = "",
            companyCatchPhrase = "",
            companyBs = "",
            cacheTimestamp = 0L
        )

        whenever(dao.getById(1)).thenReturn(entity)

        val result = repository.getUserById(1)

        assertEquals("Local", result?.name)
    }
}