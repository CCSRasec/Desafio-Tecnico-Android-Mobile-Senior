package com.seidor.data.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.seidor.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO responsável por todas as operações de persistência relacionadas a usuários.
 *
 * Este DAO segue as boas práticas do Room, oferecendo:
 *  - Inserção com upsert
 *  - Consultas reativas via Flow
 *  - Paginação simulada usando LIMIT + OFFSET
 *  - Filtros de busca por nome e e-mail
 *  - Operações auxiliares (clear, count)
 *
 * Todas as consultas são ordenadas por nome para manter consistência entre consultas completas e paginadas.
 */
@Dao
interface UserDao {

    /**
     * Insere ou atualiza usuários.
     *
     * @param items Lista de usuários a serem inseridos ou atualizados.
     */
    @Upsert
    suspend fun upsertAll(items: List<UserEntity>)

    /**
     * Apaga todos os usuários armazenados localmente.
     */
    @Query("DELETE FROM users")
    suspend fun clear()

    /**
     * Retorna a quantidade total de usuários armazenados.
     *
     * @return Quantidade de registros na tabela.
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    /**
     * Observa continuamente a lista completa de usuários,
     * filtrando opcionalmente por nome ou e-mail.
     *
     * @param q Texto de filtro (parcial); null retorna todos os usuários.
     * @return Flow emitindo a lista atualizada de usuários.
     */
    @Query("""
        SELECT * FROM users 
        WHERE (:q IS NULL OR name LIKE '%' || :q || '%' OR email LIKE '%' || :q || '%')
        ORDER BY name ASC
    """)
    fun observeAll(q: String?): Flow<List<UserEntity>>

    /**
     * Busca paginada simulada usando SQL nativo.
     * A API pública não suporta paginação real, então LIMIT/OFFSET
     * são usados para emular o comportamento no local.
     *
     * @param q Texto de busca (opcional).
     * @param limit Quantidade por página.
     * @param offset Posição inicial do próximo bloco.
     * @return Lista parcial de usuários.
     */
    @Query("""
        SELECT * FROM users 
        WHERE (:q IS NULL OR name LIKE '%' || :q || '%' OR email LIKE '%' || :q || '%')
        ORDER BY name ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun page(q: String?, limit: Int, offset: Int): List<UserEntity>

    /**
     * Retorna um único usuário pelo ID.
     *
     * @param id Identificador do usuário.
     * @return UserEntity ou null caso não exista.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?

    /**
     * Consulta reativa que filtra usuários por nome ou e-mail.
     *
     * @param query Texto parcial usado na busca.
     * @return Flow com lista filtrada.
     */
    @Query("""
        SELECT * FROM users 
        WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' 
        ORDER BY name
    """)
    fun observeUsers(query: String?): Flow<List<UserEntity>>

    /**
     * Insere uma lista de usuários substituindo registros existentes.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    /**
     * Mesmo comportamento de insertAll, porém com nome alternativo
     * usado em outras partes do projeto.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}
