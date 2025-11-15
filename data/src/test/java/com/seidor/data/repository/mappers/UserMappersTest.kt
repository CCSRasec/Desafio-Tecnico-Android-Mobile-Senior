package com.seidor.data.repository.mappers

import com.seidor.data.model.AddressApi
import com.seidor.data.model.CompanyApi
import com.seidor.data.model.GeoApi
import com.seidor.data.model.UserApi
import com.seidor.data.model.UserEntity
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes unitários responsáveis por validar os mapeamentos entre:
 *
 *  - UserEntity (Room)  →  User (Domain)
 *  - UserApi (DTO da API) → UserEntity (persistência local)
 *
 * O objetivo deste arquivo é garantir que:
 *
 *  1. Os dados sejam convertidos corretamente entre camadas
 *  2. Nenhuma propriedade seja perdida ou mapeada incorretamente
 *  3. A integridade do modelo seja mantida do início ao fim do fluxo
 *
 * Esses testes são essenciais pois erros de mapeamento são silenciosos
 * e costumam gerar inconsistências difíceis de rastrear.
 */
class UserMappersTest {

    /**
     * TESTE 1 — entity.toDomain()
     *
     * Objetivo:
     *  Validar se o mapeamento de UserEntity (camada local/Room)
     *  para User (model da camada domain) está correto.
     *
     * Estratégia:
     *  - Criamos uma entidade completa simulando um registro do Room
     *  - Chamamos entity.toDomain()
     *  - Validamos campo por campo crítico do domínio
     */
    @Test
    fun `entityToDomain maps correctly`() {

        // Entidade simulada (como viria do Room)
        val entity = UserEntity(
            id = 1,
            name = "Leanne Graham",
            username = "Bret",
            email = "leanne@teste.com",
            phone = "123456",
            website = "site.com",
            street = "Rua X",
            suite = "Apto 10",
            city = "City",
            zipcode = "00000",
            lat = "-37.5",
            lng = "81.0",
            companyName = "Company",
            companyCatchPhrase = "Frase",
            companyBs = "bs test",
            cacheTimestamp = 0L
        )

        // Mapeamento sendo testado
        val domain = entity.toDomain()

        // Validações de campos essenciais
        assertEquals("Leanne Graham", domain.name)
        assertEquals("Rua X", domain.address.street)
        assertEquals("Company", domain.company.name)
        assertEquals("-37.5", domain.address.geo.lat)
    }


    /**
     * TESTE 2 — apiDto.toEntity()
     *
     * Objetivo:
     *  Validar se UserApi (DTO recebido da API) é convertido corretamente
     *  em UserEntity (para armazenamento no Room).
     *
     * Estratégia:
     *  - Criamos um objeto UserApi simulando a resposta do servidor
     *  - Chamamos toEntity()
     *  - Conferimos se propriedades individuais foram copiadas corretamente
     */
    @Test
    fun `domainToEntity maps correctly`() {

        // DTO recebido da API
        val domain = UserApi(
            id = 2,
            name = "John",
            username = "johnny",
            email = "john@mail.com",
            phone = "9999",
            website = "john.com",
            address = AddressApi(
                street = "Street",
                suite = "Suite",
                city = "City",
                zipcode = "Zip",
                geo = GeoApi("10.0", "20.0")
            ),
            company = CompanyApi(
                name = "TestCorp",
                catchPhrase = "test",
                bs = "bs"
            )
        )

        // Quando o repositório salva no banco, ele insere o cacheTimestamp
        val entity = domain.toEntity(0L)

        // Validações principais
        assertEquals("Street", entity.street)
        assertEquals("TestCorp", entity.companyName)
        assertEquals("10.0", entity.lat)
    }
}