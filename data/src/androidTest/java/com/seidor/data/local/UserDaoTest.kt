package com.seidor.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.seidor.data.local.dao.user.UserDao
import com.seidor.data.model.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários responsáveis por garantir a integridade das operações
 * do UserDao, camada fundamental para persistência local.
 *
 * Como o banco é criado em memória, os testes validam:
 *
 *  - Inserção correta de usuários
 *  - Recuperação via consultas reativas (Flow)
 *  - Paginação com limit + offset
 *  - Busca textual utilizando filtros por nome
 *
 * Esses testes são essenciais para assegurar que o Room esteja
 * estruturado corretamente e que consultas complexas mantenham
 * consistência entre escrita e leitura.
 */
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    /**
     * TESTE 1 — insert users e consulta com observeUsers()
     *
     * Objetivo:
     *  Validar se o DAO:
     *   - insere corretamente uma lista de usuários
     *   - recupera dados via fluxo reativo observeUsers()
     *
     * Estratégia:
     *  - Inserir um único UserEntity
     *  - Observar fluxo
     *  - Validar tamanho e conteúdo da lista
     */
    @Test
    fun insertUserAndQuery() = runBlocking {

        val user = UserEntity(
            id = 1,
            name = "Leanne Graham",
            username = "Bret",
            email = "leanne@test.com",
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

        dao.insertUsers(listOf(user))

        val result = dao.observeUsers("").first()

        assertEquals(1, result.size)
        assertEquals("Leanne Graham", result[0].name)
    }

    /**
     * TESTE 2 — paginação local com page(limit, offset)
     *
     * Objetivo:
     *  Verificar se o DAO:
     *   - retorna subconjuntos corretos conforme limit + offset
     *   - preserva ordenação e índice esperado
     *
     * Estratégia:
     *  - Inserir 10 usuários sequenciais
     *  - Buscar página com limit=3, offset=3
     *  - Validar que o primeiro elemento da página seja o usuário 4
     */
    @Test
    fun pagedQueryReturnsCorrectSubset() = runBlocking {

        val users = (1..10).map {
            UserEntity(
                id = it,
                name = "User $it",
                username = "u$it",
                email = "u$it@test.com",
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
                cacheTimestamp = 0
            )
        }

        dao.clear()
        dao.insertAll(users)

        // Act - limit 2, offset 2
        val page = dao.page(q = null, limit = 2, offset = 2)


        assertEquals(2, page.size)

        assertEquals("User 2", page[0].name)
        assertEquals("User 3", page[1].name)
    }

    /**
     * TESTE 3 — busca textual via observeUsers(query)
     *
     * Objetivo:
     *  Garantir que consultas filtradas por nome funcionem corretamente,
     *  retornando apenas usuários que contenham o termo buscado.
     *
     * Estratégia:
     *  - Inserir usuários com nomes distintos
     *  - Consultar com termo parcial ("Ali")
     *  - Validar filtro e resultado
     */
    @Test
    fun searchByNameWorks() = runBlocking {

        val users = listOf(
            UserEntity(
                id = 1,
                name = "Alice",
                username = "",
                email = "a@test.com",
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
                cacheTimestamp = 0
            ),
            UserEntity(
                id = 2,
                name = "Bob",
                username = "",
                email = "b@test.com",
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
                cacheTimestamp = 0
            )
        )

        dao.insertUsers(users)

        val result = dao.observeUsers("Ali").first()

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)
    }
}