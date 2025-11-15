# Desafio Técnico – Aplicativo Android (Mobile Sênior)

Este repositório contém a implementação completa do desafio técnico proposto para a posição de **Desenvolvedor(a) Mobile Sênior**, utilizando **Kotlin**, **Clean Architecture**, **Jetpack Compose**, **Room**, **Coroutines**, **Flow**, e **Koin**.

---

## Sobre o Aplicativo

O aplicativo consome a API pública:

```
https://jsonplaceholder.typicode.com/users
```

E fornece:

- Listagem de usuários com **nome**, **email** e **cidade**
- **Busca** por nome ou e‑mail
- Lista com **paginação local simulada**
- **Pull‑to‑refresh**
- Tela de **detalhes do usuário**
- **Persistência local** (Room)
- Tratamento de erros de rede e parsing

---

## Arquitetura

O projeto segue **Clean Architecture + MVVM**, dividido em módulos:

```
app/
data/
domain/
```

### domain
- Models
- UseCases
- Regras de negócio

### data
- Repositórios
- Retrofit
- Room
- Mappers

### app
- UI com Jetpack Compose
- Fragments + ComposeView
- ViewModels
- Navegação

---

## Tecnologias Utilizadas

- **Kotlin**
- **Coroutines + Flow**
- **Room 2.6.1**
- **Retrofit / OkHttp**
- **Jetpack Compose**
- **Koin (DI)**
- **Navigation Component**
- **Material 3**
- Testes unitários (JUnit + Turbine + Mockito)
- Testes instrumentados (Room testing + AndroidX Runner)

---

## Testes

O projeto inclui:

### Testes unitários
- Mapeamentos (API → Entity → Domain)
- DAO Room (in‑memory database)
- ViewModel
- Paginação simulada
- Fluxos reativos usando Flow

### Testes instrumentados
- Room + SQLite KTX

---

##  Como Executar o Projeto

### Requisitos
- Android Studio Ladybug ou superior
- JDK 17
- Gradle 8+
- Emulador ou dispositivo Android

### Passos
1. Clone o repositório:
```
git clone https://github.com/CCSRasec/Desafio-T-cnico-Aplicativo-Android-Mobile-S-nior-.git
```

2. Abra no Android Studio

3. Rode o app normalmente pelo botão **Run**

---

## Estrutura das Telas

### Tela de Lista
- Lista de usuários
- Campo de busca
- Pull‑to‑refresh
- Paginação

### Tela de Detalhes
- Mostra **todas** as informações do usuário:
  - Nome
  - Username
  - Email
  - Endereço (rua, suite, cidade, zipcode)
  - Geo (lat/lng)
  - Empresa (nome, catchPhrase, bs)
  - Telefone
  - Website

---

## Execução da Paginação
A API não possui paginação real, portanto foi implementada paginação **local** simulada usando:

```
LIMIT x OFFSET y
```

---

## Tratamento de Erros

O app cobre:
- Falha de rede
- Timeout
- Parsing inválido
- Falha na sincronização com fallback local
- Estado de erro em tela cheia quando não há dados no banco

---

## Injeção de Dependência

Utilizado **Koin**, com módulos:
- `networkModule`
- `repositoryModule`
- `databaseModule`
- `viewModelModule`
- `useCaseModule`

---

## Tempo Estimado de Desenvolvimento

**Aproximadamente 12 horas** incluindo:
- Estruturação do projeto
- Implementação
- Testes
- Documentação

---

## Licença

Este projeto é apenas para fins de avaliação técnica.

---

Para qualquer dúvida, fico à disposição.
